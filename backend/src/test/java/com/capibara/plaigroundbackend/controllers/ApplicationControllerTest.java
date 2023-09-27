package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.PlAIgroundBackendApplication;
import com.capibara.plaigroundbackend.models.*;
import com.capibara.plaigroundbackend.models.dtos.*;
import com.capibara.plaigroundbackend.repositories.ApplicationRepository;
import com.capibara.plaigroundbackend.repositories.CategoryRepository;
import com.capibara.plaigroundbackend.repositories.LLMSettingRepository;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import com.capibara.plaigroundbackend.services.AiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PlAIgroundBackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApplicationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private UserEntityRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CategoryRepository categoryRepo;
    @Autowired
    private LLMSettingRepository llmSettingRepository;
    @MockBean
    private AiService aiService;

    @BeforeEach
    void cleanUp() {
        appRepo.deleteAll();
        userRepo.deleteAll();
        categoryRepo.deleteAll();
        llmSettingRepository.deleteAll();
    }

    @Test
    void updateApp() {
        Category category = Category.builder()
                .name("Test category")
                .build();

        categoryRepo.save(category);

        UserEntity owner = UserEntity.builder()
                .nickname("user1")
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .isEnabled(true)
                .appList(new ArrayList<>())
                .build();

        userRepo.save(owner);

        Application appToUpdate = Application.builder()
                .name("Application")
                .category(null)
                .description("Test application")
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(false)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();

        appRepo.save(appToUpdate);

        HttpHeaders headersLogin = new HttpHeaders();

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/login", port),
                        HttpMethod.POST,
                        httpEntityLogin,
                        AuthenticationResponse.class);


        Application expected = Application.builder()
                .id(1L).name("App")
                .category(category)
                .description("Test")
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(true)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());

        UpdateAppDTO updateAppDTO = UpdateAppDTO.builder()
                .name("App")
                .category("Test category")
                .description("Test")
                .isPublic(true).build();

        HttpEntity<UpdateAppDTO> httpEntity = new HttpEntity<>(updateAppDTO, headers);

        ResponseEntity<ApplicationDto> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/applications/1", port),
                        HttpMethod.PUT,
                        httpEntity,
                        ApplicationDto.class);

        assertEquals(expected.getName(), response.getBody().getName());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updateAppWrongUser() {
        Category category = Category.builder()
                .name("Test category")
                .build();

        categoryRepo.save(category);

        UserEntity owner = UserEntity.builder()
                .nickname("user1")
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .appList(new ArrayList<>())
                .build();

        UserEntity notOwner = UserEntity.builder()
                .nickname("user2")
                .email("test2@test.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .appList(new ArrayList<>()).build();

        userRepo.save(owner);
        userRepo.save(notOwner);

        Application appToUpdate = Application.builder()
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

        appRepo.save(appToUpdate);

        HttpHeaders headersLogin = new HttpHeaders();
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test2@test.com")
                .password("password")
                .build();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/login", port),
                        HttpMethod.POST,
                        httpEntityLogin,
                        AuthenticationResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());

        UpdateAppDTO updateAppDTO = UpdateAppDTO.builder()
                .name("App")
                .description("Test")
                .category("Test category")
                .isPublic(true)
                .build();

        HttpEntity<UpdateAppDTO> httpEntity = new HttpEntity<>(updateAppDTO, headers);

        ResponseEntity<String> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/applications/1", port),
                        HttpMethod.PUT,
                        httpEntity,
                        String.class);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    void updateAppNoData() {
        UserEntity owner = UserEntity.builder()
                .nickname("user1")
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .isEnabled(true)
                .appList(new ArrayList<>())
                .build();

        userRepo.save(owner);

        Application appToUpdate = Application.builder()
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

        appRepo.save(appToUpdate);

        HttpHeaders headersLogin = new HttpHeaders();

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/login", port),
                        HttpMethod.POST,
                        httpEntityLogin,
                        AuthenticationResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());

        UpdateAppDTO updateAppDTO = UpdateAppDTO.builder()
                .name(null)
                .category("")
                .description("Test")
                .isPublic(true)
                .build();

        HttpEntity<UpdateAppDTO> httpEntity = new HttpEntity<>(updateAppDTO, headers);

        ResponseEntity<String> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/applications/1", port),
                        HttpMethod.PUT,
                        httpEntity,
                        String.class);

        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return app when app with given id exists and user is owner")
    void getAppById() {
        UserEntity owner = UserEntity.builder()
                .nickname("user1")
                .email("tes@test.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .isEnabled(true)
                .appList(new ArrayList<>())
                .build();
        userRepo.save(owner);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("tes@test.com")
                .password("password")
                .build();

        HttpHeaders headersLogin = new HttpHeaders();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/login", port),
                        HttpMethod.POST,
                        httpEntityLogin,
                        AuthenticationResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());


        Application app = Application.builder()
                .name("App")
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(false)
                .build();

        appRepo.save(app);

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ApplicationDto> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/applications/1", port),
                        HttpMethod.GET,
                        httpEntity,
                        ApplicationDto.class);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Should return exception when app owner is not logged in")
    void getAppByIdReturnExceptionWhenUserNotLoggedIn() {
        UserEntity owner = UserEntity.builder()
                .nickname("user1")
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .build();
        userRepo.save(owner);

        UserEntity owner2 = UserEntity.builder()
                .nickname("user2")
                .email("test2@test.com")
                .password(passwordEncoder.encode("test"))
                .role(Role.USER)
                .build();
        userRepo.save(owner2);

        Application app = Application.builder()
                .name("App")
                .description("Test")
                .owner(owner2)
                .prompt("Test prompt")
                .isPublic(false)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();
        app = appRepo.save(app);

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();

        HttpHeaders headersLogin = new HttpHeaders();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/login", port),
                        HttpMethod.POST,
                        httpEntityLogin,
                        AuthenticationResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());

        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate
                .exchange(String.format("http://localhost:%s/api/applications/%s", port, app.getId()),
                        HttpMethod.GET,
                        httpEntity,
                        String.class);

        assertEquals(403, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test with correct input")
    void createAppOk() {
        final String aiResponse = """
                HTML>>\n<!DOCTYPE html>\n<html>\n<head>\n  <title>Add Two Integers</title>\n  <style><<HTML\n\n
                CSS>>\n\nbody {\n  font-family: Arial, sans-serif;\n  background-color: #f2f2f2;\n}<<CSS\n\n
                SCRIPT>>\nfunction addNumbers() {\n  var num1 = document.getElementById('num1').value;\n<<SCRIPT""";
        when(aiService.getAiResponse(any(ChatCompletionRequest.class))).thenReturn(aiResponse);

        UserEntity owner = UserEntity
                .builder()
                .nickname("user1")
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .role(Role.USER)
                .isEnabled(true)
                .appList(new ArrayList<>())
                .build();

        userRepo.save(owner);
        final String conditions =
                "The HTML part of the code should be surrounded by HTML>> <<HTML marks." +
                "Means that CSS part of the code should should start with mark HTML>> and end with <<HTML mark" +
                "The style part of the code should be surrounded by CSS>> <<CSS marks." +
                "Means that CSS part of the code should should start with mark CSS>> and end with <<CSS mark" +
                "The script part of the code should be surrounded by SCRIPT>> <<SCRIPT marks." +
                "Means that script part of the code should should start with mark SCRIPT>> and end with <<SCRIPT mark" +
                "Response should mandatory contain all three parts.";

        LLMSetting llmSetting = new LLMSetting();
        llmSetting.setTemperature(0.4);
        llmSetting.setSystemMessage("You are a web application developer. " +
                                    "Write an application according to the user specification. " +
                                    "Application will run in web browser. " +
                                    "Write the code without comments. " +
                                    "Separate the answer into 3 parts. " +
                                    "Use single quotes for parameters. " +
                                    "Don't write the style directly into the html code." +
                                    "Use provided by user name in HTML code. " +
                                    conditions);
        llmSetting.setDefaultModel(true);
        llmSettingRepository.save(llmSetting);

        HttpHeaders headersLogin = new HttpHeaders();

        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@test.com")
                .password("password")
                .build();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate
                .exchange(String.format("http://localhost:%s/api/auth/login", port),
                        HttpMethod.POST, httpEntityLogin, AuthenticationResponse.class);

        Application expected = Application.builder()
                .id(1L).name("App")
                .description("Test")
                .owner(owner)
                .prompt("Test prompt")
                .isPublic(true)
                .html(" <!DOCTYPE html> <html> <head>   <title>Add Two Integers</title>   <style>")
                .css("  body {   font-family: Arial, sans-serif;   background-color: #f2f2f2; }")
                .javaScript(" function addNumbers() {   var num1 = document.getElementById('num1').value; ")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());

        RequestForNewAppDto requestDTO = RequestForNewAppDto.builder()
                .name("App")
                .description("Test")
                .prompt("Test prompt")
                .build();

        HttpEntity<RequestForNewAppDto> httpEntity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<ApplicationDto> response = restTemplate.exchange(String.format("http://localhost:%s/api/applications", port), HttpMethod.POST, httpEntity, ApplicationDto.class);

        assertEquals(expected.getName(), response.getBody().getName());
        assertEquals(expected.getDescription(), response.getBody().getDescription());
        assertEquals(expected.getPrompt(), response.getBody().getPrompt());
        assertEquals(expected.getHtml(), response.getBody().getHtml());
        assertEquals(expected.getCss(), response.getBody().getCss());
        assertEquals(expected.getJavaScript(), response.getBody().getJavaScript());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test with missing part of the code")
    void createAppMissingCode() {
        final String aiResponse = """
                TML>>\n<!DOCTYPE html>\n<html>\n<head>\n  <title>Add Two Integers</title>\n  <style><<HTML\n\n
                CSS>>\n\nbody {\n  font-family: Arial, sans-serif;\n  background-color: #f2f2f2;\n}<<CSS\n\n
                SCRIPT>>\nfunction addNumbers() {\n  var num1 = document.getElementById('num1').value;\n<<SCRIPT""";
        when(aiService.getAiResponse(any(ChatCompletionRequest.class))).thenReturn(aiResponse);

        UserEntity owner = UserEntity.builder().nickname("user1").email("test@test.com").password(passwordEncoder.encode("password")).role(Role.USER).isEnabled(true).appList(new ArrayList<>()).build();

        userRepo.save(owner);

        HttpHeaders headersLogin = new HttpHeaders();

        LoginRequest loginRequest = LoginRequest.builder().email("test@test.com").password("password").build();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.exchange(String.format("http://localhost:%s/api/auth/login", port), HttpMethod.POST, httpEntityLogin, AuthenticationResponse.class);


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());

        RequestForNewAppDto requestDTO = RequestForNewAppDto.builder().name("App").description("Test").prompt("Test prompt").build();

        HttpEntity<RequestForNewAppDto> httpEntity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(String.format("http://localhost:%s/api/applications", port), HttpMethod.POST, httpEntity, String.class);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Test with incomplete request")
    void createAppIncompleteRequest() {
        final String aiResponse = """
                HTML>>\n<!DOCTYPE html>\n<html>\n<head>\n  <title>Add Two Integers</title>\n  <style><<HTML\n\n
                CSS>>\n\nbody {\n  font-family: Arial, sans-serif;\n  background-color: #f2f2f2;\n}<<CSS\n\n
                SCRIPT>>\nfunction addNumbers() {\n  var num1 = document.getElementById('num1').value;\n<<SCRIPT""";
        when(aiService.getAiResponse(any(ChatCompletionRequest.class))).thenReturn(aiResponse);

        UserEntity owner = UserEntity.builder().nickname("user1").email("test@test.com").password(passwordEncoder.encode("password")).role(Role.USER).isEnabled(true).appList(new ArrayList<>()).build();

        userRepo.save(owner);

        HttpHeaders headersLogin = new HttpHeaders();

        LoginRequest loginRequest = LoginRequest.builder().email("test@test.com").password("password").build();

        HttpEntity<LoginRequest> httpEntityLogin = new HttpEntity<>(loginRequest, headersLogin);

        ResponseEntity<AuthenticationResponse> loginResponse = restTemplate.exchange(String.format("http://localhost:%s/api/auth/login", port), HttpMethod.POST, httpEntityLogin, AuthenticationResponse.class);


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(loginResponse.getBody().getAccessToken());

        RequestForNewAppDto requestDTO = RequestForNewAppDto.builder().name("App").description("Test").build();

        HttpEntity<RequestForNewAppDto> httpEntity = new HttpEntity<>(requestDTO, headers);

        ResponseEntity<String> response = restTemplate.exchange(String.format("http://localhost:%s/api/applications", port), HttpMethod.POST, httpEntity, String.class);

        assertEquals(400, response.getStatusCodeValue());
    }
}
