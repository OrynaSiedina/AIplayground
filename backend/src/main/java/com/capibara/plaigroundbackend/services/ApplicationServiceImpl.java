package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.AccessForbiddenException;
import com.capibara.plaigroundbackend.exceptions.DataRequiredException;
import com.capibara.plaigroundbackend.exceptions.NotFoundException;
import com.capibara.plaigroundbackend.exceptions.ServerException;
import com.capibara.plaigroundbackend.models.*;
import com.capibara.plaigroundbackend.models.dtos.ApplicationDto;
import com.capibara.plaigroundbackend.models.dtos.RequestForNewAppDto;
import com.capibara.plaigroundbackend.models.dtos.UpdateAppDTO;
import com.capibara.plaigroundbackend.repositories.ApplicationRepository;
import com.capibara.plaigroundbackend.repositories.CategoryRepository;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {
    private final AiService aiService;
    private final ApplicationRepository appRepo;
    private final UserEntityRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final PrincipalService principalService;
    private final LLMSettingService llmSettingService;


    @Override
    public Application create(RequestForNewAppDto request) {

        ChatCompletionRequest chatRequest = createChatRequest(request);
        String response = aiService.getAiResponse(chatRequest);
        Application app = build(request, response);

        try {
            return appRepo.save(app);
        } catch (Exception e) {
            throw new ServerException("Saving to database failed");
        }
    }

    @Override
    public ChatCompletionRequest createChatRequest(RequestForNewAppDto request) {
        final List<ChatMessage> messages = new ArrayList<>();
        LLMSetting defaultSetting = llmSettingService.findDefaultLLMSetting();
        if (request.getPrompt() == null && request.getName() == null && request.getDescription() == null) {
            throw new DataRequiredException("All Fields");
        }
        if (request.getPrompt() == null || request.getPrompt().isEmpty()) {
            throw new DataRequiredException("Requirements");
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new DataRequiredException("Application Name");
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new DataRequiredException("Description");
        }
        String userPrompt =
                "Create application with name " + request.getName() + " that user described like this: " + request.getDescription() + ". This is the users prompt: " + request.getPrompt() +
                        ". Provide HTML, CSS, Java Script parts as it specified in system prompt." +
                        " In the HTML part, include the name provided by the user." +
                        " The styling of the components should be inside the CSS part required by system prompt.";

        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), defaultSetting.getSystemMessage());
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
        messages.add(systemMessage);
        messages.add((userMessage));
        return ChatCompletionRequest
                .builder()
                .model(defaultSetting.getChatModel())
                .messages(messages)
                .n(1)
                .temperature(defaultSetting.getTemperature())
                .logitBias(new HashMap<>())
                .build();
    }

    @Override
    public Application build(RequestForNewAppDto request, String response) {
        String usersEmail = principalService.getPrincipalName();
        Optional<UserEntity> owner = userRepo.findByEmail(usersEmail);

        if (owner.isEmpty()) {
            throw new ServerException("Loading owner from the database failed");
        }

        LLMSetting setting = llmSettingService.findDefaultLLMSetting();

        return Application.builder()
                .owner(owner.get())
                .category(null)
                .name(request.getName())
                .description(request.getDescription())
                .prompt(request.getPrompt())
                .isPublic(false)
                .html(parseResponse(response, "HTML"))
                .css(parseResponse(response, "CSS"))
                .javaScript(parseResponse(response, "SCRIPT"))
                .LLMSetting(setting.getId() == null ? null : setting)
                .build();
    }

    @Override
    public String parseResponse(String input, String marker) {
        String clearedInput = input.replaceAll("\n", " ");
        if (!input.contains(marker + ">>") || !input.contains("<<" + marker)) {
            throw new ServerException("Ai response doesn't contain " + marker + " part");
        }
        int beginning = clearedInput.indexOf(marker + ">>") + marker.length() + 2;
        int end = clearedInput.indexOf("<<" + marker);
        return clearedInput.substring(beginning, end);
    }

    @Override
    public Application update(UpdateAppDTO updateAppDTO, Long id) {
        Optional<Application> optionalApp = appRepo.findById(id);
        Optional<Category> optionalCategory = categoryRepo.findByName(updateAppDTO.getCategory());

        if (optionalApp.isEmpty()) {
            throw new NotFoundException("Application");
        }

        Application app = optionalApp.get();

        if (!app.getOwner().getEmail().equals(principalService.getPrincipalName())) {
            throw new AccessForbiddenException("You are not the owner of this application");
        }

        if (updateAppDTO.getName() == null) {
            throw new DataRequiredException("Name");
        }
        if (updateAppDTO.getDescription() == null) {
            throw new DataRequiredException("Description");
        }

        app.setName(updateAppDTO.getName());
        app.setDescription(updateAppDTO.getDescription());
        app.setPublic(Boolean.TRUE.equals(updateAppDTO.isPublic()));
        optionalCategory.ifPresent(app::setCategory);

        return appRepo.save(app);
    }

    @Override
    public Application getById(Long id) {

        String usersEmail = principalService.getPrincipalName();

        Application application = appRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Application"));

        if (application.getOwner().getEmail().equals(usersEmail) ||
                application.isPublic() ||
                application.getOwner().getRole().equals(Role.ADMIN)) {
            return application;
        } else throw new AccessForbiddenException();
    }

    @Override
    public List<ApplicationDto> getUsersApps() {
        String userEmail = principalService.getPrincipalName();
        UserEntity owner = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User"));

        List<ApplicationDto> applicationDtos = appRepo.findAllByOwner(owner).stream()
                .map(ApplicationDto::new)
                .collect(Collectors.toList());

        Collections.reverse(applicationDtos);
        return applicationDtos;
    }

    @Override
    public Page<Application> getPublicApps(Optional<String> category, int pageNumber, int pageSize) {

        String userEmail = principalService.getPrincipalName();
        UserEntity currentUser = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User"));

        List<Long> usedAppIds = currentUser.getUsed().stream()
                .map(Application::getId)
                .toList();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        if (category.isEmpty()) {
            return appRepo.findAllByIsPublicTrueAndCategoryNotNullAndIdNotIn(usedAppIds, pageable);
        } else {
            Optional<Category> optionalCategory = categoryRepo.findByName(category.get());

            if (optionalCategory.isEmpty()) {
                throw new NotFoundException("Category");
            }
            return appRepo.findAllByIsPublicTrueAndCategoryAndIdNotIn(optionalCategory.get(), usedAppIds, pageable);
        }
    }

    @Override
    public Application delete(Long id) {
        Optional<Application> optionalApp = appRepo.findById(id);

        if (optionalApp.isEmpty()) {
            throw new NotFoundException("Application");
        }

        Application app = optionalApp.get();

        if (!(app.getOwner().getEmail().equals(principalService.getPrincipalName()) || app.getOwner().getRole().equals(Role.ADMIN))) {
            throw new AccessForbiddenException("You are not the owner of this application");
        }

        appRepo.deleteById(id);
        return app;
    }
}
