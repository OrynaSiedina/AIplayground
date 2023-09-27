package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.AccessForbiddenException;
import com.capibara.plaigroundbackend.exceptions.DataRequiredException;
import com.capibara.plaigroundbackend.exceptions.NotFoundException;
import com.capibara.plaigroundbackend.exceptions.ServerException;
import com.capibara.plaigroundbackend.models.*;
import com.capibara.plaigroundbackend.models.dtos.RequestForNewAppDto;
import com.capibara.plaigroundbackend.models.dtos.UpdateAppDTO;
import com.capibara.plaigroundbackend.repositories.ApplicationRepository;
import com.capibara.plaigroundbackend.repositories.CategoryRepository;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static com.capibara.plaigroundbackend.models.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class ApplicationServiceTest {

    @Autowired
    private ApplicationService appService;

    @MockBean
    private AiService aiService;

    @MockBean
    private UserEntityRepository userRepo;

    @MockBean
    private ApplicationRepository appRepo;

    @MockBean
    private CategoryRepository categoryRepo;

    @MockBean
    private LLMSettingServiceImpl llmSettingServiceImpl;

    //Openai response
    final String response = """
            HTML>>\n<!DOCTYPE html>\n<html>\n<head>\n  <title>Add Two Integers</title>\n  <style><<HTML\n\n
            CSS>>\n\nbody {\n  font-family: Arial, sans-serif;\n  background-color: #f2f2f2;\n}<<CSS\n\n
            SCRIPT>>\nfunction addNumbers() {\n  var num1 = document.getElementById('num1').value;\n<<SCRIPT""";
    //request for new Application
    private final RequestForNewAppDto request = RequestForNewAppDto.builder()
            .name("Application")
            .description("Test application")
            .prompt("Test prompt")
            .build();
    final UserEntity userToFind = UserEntity.builder()
            .id(1L)
            .nickname("User1")
            .email("josef.vomacka@email.xy")
            .password("tajneheslo")
            .role(Role.USER)
            .appList(new ArrayList<>())
            .build();
    final Application expectedApp = Application.builder()
            .name("Application")
            .description("Test application")
            .owner(userToFind)
            .prompt("Test prompt")
            .isPublic(false)
            .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
            .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
            .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
            .build();

    LLMSetting llmSetting = LLMSetting.builder()
            .id(1L)
            .temperature(0.4)
            .systemMessage("system message")
            .defaultModel(true)
            .build();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(llmSettingServiceImpl.findDefaultLLMSetting()).thenReturn(llmSetting);
    }

    @Test
    @DisplayName("Integration test createApp with correct values")
    @WithMockUser(username = "email@email.xy")
    void createAppTestOk() {

        when(aiService.getAiResponse(any(ChatCompletionRequest.class))).thenReturn(response);
        when(appRepo.save(any(Application.class))).then(returnsFirstArg());
        when(userRepo.findByEmail("email@email.xy")).thenReturn(Optional.ofNullable(userToFind));
        Application actualApp = appService.create(request);

        assertEquals(expectedApp.getName(), actualApp.getName());
    }

    @Test
    @DisplayName("Integration test doesn't createApp with incorrect user email")
    @WithMockUser(username = "mail@email.xy")
    void createAppTestNok() {

        when(aiService.getAiResponse(any(ChatCompletionRequest.class))).thenReturn(response);
        when(appRepo.save(any(Application.class))).then(returnsFirstArg());
        when(userRepo.findByEmail("email@email.xy")).thenReturn(Optional.ofNullable(userToFind));
        ServerException exception = assertThrows(ServerException.class, () -> {
            appService.create(request);
        });
        assertEquals("Loading owner from the database failed", exception.getMessage());
    }

    @Test
    @DisplayName("Unit test create ChatRequest with correct values")
    void createChatRequestOk() {
        RequestForNewAppDto request = new RequestForNewAppDto();
        request.setPrompt("Test prompt");
        request.setName("Test Application");
        request.setDescription("Test Description");

        String userPrompt =
                "Create application with name " + request.getName() + " that user described like this: " + request.getDescription() + ". This is the users prompt: " + request.getPrompt() +
                        ". Provide HTML, CSS, Java Script parts as it specified in system prompt." +
                        " In the HTML part, include the name provided by the user." +
                        " The styling of the components should be inside the CSS part required by system prompt.";
        List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), llmSetting.getSystemMessage());
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);

        messages.add(systemMessage);
        messages.add((userMessage));
        ChatCompletionRequest expectedRequest = ChatCompletionRequest.builder()
                .model(llmSetting.getChatModel())
                .messages(messages)
                .n(1)
                .temperature(llmSetting.getTemperature())
                .logitBias(new HashMap<>())
                .build();

        ChatCompletionRequest actualRequest = appService.createChatRequest(request);

        assertEquals(expectedRequest, actualRequest);
    }

    @Test
    @DisplayName("Unit test createChatRequest with missing prompt")
    void createChatRequestNok() {

        RequestForNewAppDto badRequest = RequestForNewAppDto.builder()
                .name("Application")
                .description("Test application")
                .build();

        DataRequiredException exception = assertThrows(DataRequiredException.class, () -> {
            appService.createChatRequest(badRequest);
        });
        assertEquals("You are missing required field(s): Requirements", exception.getMessage());
    }

    @Test
    @DisplayName("Unit test buildApp with correct values")
    @WithMockUser(username = "pepa.vomacka@email.xy")
    void buildAppOk() {
        when(userRepo.findByEmail("pepa.vomacka@email.xy")).thenReturn(Optional.of(userToFind));
        Application actualApp = appService.build(request, response);
        assertEquals(expectedApp.getName(), actualApp.getName());
    }

    @Test
    @DisplayName("Unit test buildApp with invalid user email")
    @WithMockUser(username = "pepa.omacka@email.xy")
    void buildAppNok() {
        when(userRepo.findByEmail("pepa.vomacka@email.xy")).thenReturn(Optional.of(userToFind));

        ServerException exception = assertThrows(ServerException.class, () -> {
            appService.build(request, response);
        });
        assertEquals("Loading owner from the database failed", exception.getMessage());
    }

    @Test
    @DisplayName("Unit test parseResponse with correct values")
    void parseResponseTestOk() {

        String expectedHtml = " <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>";
        String expectedCss = "  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }";
        String expectedJs = " function addNumbers() {   var num1 = document.getElementById('num1').value; ";

        String actualHtml = appService.parseResponse(response, "HTML");
        String actualCss = appService.parseResponse(response, "CSS");
        String actualJS = appService.parseResponse(response, "SCRIPT");

        assertEquals(expectedHtml, actualHtml);
        assertEquals(expectedCss, actualCss);
        assertEquals(expectedJs, actualJS);
    }

    @Test
    @DisplayName("Unit test parseResponse with missing parts")
    void parseResponseTestNok() {
        final String badResponse = """
                HTM>>\n<!DOCTYPE html>\n<html>\n<head>\n  <title>Add Two Integers</title>\n  <style><<HTML\n\n
                CS>>\n\nbody {\n  font-family: Arial, sans-serif;\n  background-color: #f2f2f2;\n}<<CSS\n\n
                SCRIP>>\nfunction addNumbers() {\n  var num1 = document.getElementById('num1').value;\n<<SCRIPT""";


        ServerException exceptionHTML = assertThrows(ServerException.class, () -> {
            appService.parseResponse(badResponse, "HTML");
        });
        assertEquals("Ai response doesn't contain HTML part", exceptionHTML.getMessage());

        ServerException exceptionCSS = assertThrows(ServerException.class, () -> {
            appService.parseResponse(badResponse, "CSS");
        });
        assertEquals("Ai response doesn't contain CSS part", exceptionCSS.getMessage());

        ServerException exceptionJS = assertThrows(ServerException.class, () -> {
            appService.parseResponse(badResponse, "SCRIPT");
        });
        assertEquals("Ai response doesn't contain SCRIPT part", exceptionJS.getMessage());
    }

    @Test
    @WithMockUser(username = "vokurka")
    @DisplayName("Update app")
    void updateApp() {
        Category category = Category.builder()
                .id(1L)
                .name("movies")
                .build();

        UserEntity owner = UserEntity.builder()
                .id(1L)
                .nickname("user1")
                .email("vokurka")
                .password("password")
                .role(Role.USER)
                .appList(new ArrayList<>())
                .build();

        Application appToUpdate = Application.builder()
                .id(1L)
                .name("Application")
                .description("Test application")
                .category(null)
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(false)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();

        UpdateAppDTO updateAppDTO = UpdateAppDTO.builder()
                .name("App")
                .description("Test")
                .category("movies")
                .isPublic(true)
                .build();

        Application expected = Application.builder()
                .id(1L)
                .name("App")
                .description("Test")
                .category(category)
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(true)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();

        when(categoryRepo.findByName(anyString())).thenReturn(Optional.of(category));

        when(appRepo.findById(anyLong())).thenReturn(Optional.of(appToUpdate));

        when(appRepo.save(any(Application.class))).then(returnsFirstArg());

        Application actual = appService.update(updateAppDTO, 1L);

        assertEquals(actual.getName(), expected.getName());
    }

    @Test
    @WithMockUser(username = "vokurka")
    @DisplayName("Update app with wrong user")
    void updateAppWrongUser() {
        UserEntity owner = UserEntity.builder()
                .id(1L)
                .nickname("user1")
                .email("vok")
                .password("password")
                .role(Role.USER)
                .appList(new ArrayList<>())
                .build();

        Application appToUpdate = Application.builder()
                .id(1L)
                .name("Application")
                .description("Test application")
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(false)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();

        UpdateAppDTO updateAppDTO = UpdateAppDTO.builder()
                .name("App")
                .description("Test")
                .isPublic(true)
                .build();

        when(appRepo.findById(anyLong())).thenReturn(Optional.of(appToUpdate));

        AccessForbiddenException exception = Assertions.assertThrows(AccessForbiddenException.class, () -> {
            appService.update(updateAppDTO, 1L);
        });

        assertEquals("You are not the owner of this application", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "vokurka")
    @DisplayName("Update app but no app exists")
    void updateAppNoAppExists() {
        UpdateAppDTO updateAppDTO = UpdateAppDTO.builder()
                .name("App")
                .description("Test")
                .isPublic(true)
                .build();

        when(appRepo.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class, () -> {
            appService.update(updateAppDTO, 1L);
        });

        assertEquals("Application not found", exception.getMessage());
    }

    @Test
    @WithMockUser(username = "vokurka")
    @DisplayName("Update app without name")
    void updateAppWithoutName() {
        UserEntity owner = UserEntity.builder()
                .id(1L)
                .nickname("user1")
                .email("vokurka")
                .password("password")
                .role(Role.USER)
                .appList(new ArrayList<>())
                .build();

        Application appToUpdate = Application.builder()
                .id(1L)
                .name("Application")
                .description("Test application")
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(false)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();

        UpdateAppDTO updateAppDTO = UpdateAppDTO.builder()
                .name(null)
                .description("Test")
                .isPublic(true)
                .build();

        when(appRepo.findById(anyLong())).thenReturn(Optional.of(appToUpdate));

        DataRequiredException exception = Assertions.assertThrows(DataRequiredException.class, () -> {
            appService.update(updateAppDTO, 1L);
        });

        assertEquals("You are missing required field(s): Name", exception.getMessage());
    }

    @Test
    @DisplayName("Should return app when app with given id exists and user is owner")
    @WithMockUser(username = "test@test.com")
    void getAppByIdWhenAppExistAndUserIsOwner() {
        Long id = 1L;
        Application app = new Application();
        app.setId(id);

        UserEntity owner = new UserEntity();
        owner.setEmail("test@test.com");
        app.setOwner(owner);

        when(appRepo.findById(id)).thenReturn(Optional.of(app));

        Application result = appService.getById(id);

        assertNotNull(result);
        assertEquals(app, result);
        assertEquals(1, result.getId());
    }

    @Test
    @DisplayName("Should throw exception when app with given id is not found")
    @WithMockUser(username = "test@test.com")
    void getAppByIdThrowsExceptionWhenAppIsNotFound() {
        Long id = 1L;
        when(appRepo.findById(id)).thenReturn(Optional.empty());

        assertEquals("Application not found", assertThrows
                (NotFoundException.class, () -> appService.getById(id)).getMessage());
    }

    @Test
    @DisplayName("Should throw exception when user is not owner of app or user is not admin")
    @WithMockUser(username = "test@test.com")
    void getAppByIdThrowsExceptionWhenAccessIfForbidden() {
        Long id = 1L;
        Application app = new Application();
        app.setPublic(false);
        UserEntity owner = new UserEntity();
        owner.setEmail("other@test.com");
        owner.setRole(USER);
        app.setOwner(owner);

        when(appRepo.findById(id)).thenReturn(Optional.of(app));

        assertEquals("Access Forbidden", assertThrows
                (AccessForbiddenException.class, () -> appService.getById(id)).getMessage());
    }
}
