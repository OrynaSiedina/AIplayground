package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.NotFoundException;
import com.capibara.plaigroundbackend.models.Application;
import com.capibara.plaigroundbackend.models.Category;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.models.VerificationToken;
import com.capibara.plaigroundbackend.repositories.ApplicationRepository;
import com.capibara.plaigroundbackend.repositories.CategoryRepository;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;


@SpringBootTest
public class ApplicationServiceIntTest {

    @Autowired
    private ApplicationService appService;

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserEntityRepository userRepo;

    @BeforeEach
    void setUp() {
        doAnswer(invocation -> {
            System.out.println("Sending email to: " + invocation.getArgument(0));
            return null;  // This is fine for a void method.
        }).when(emailService).send(anyString(), anyString(), anyString());
        when(emailService.createVerificationToken()).thenReturn(new VerificationToken());
        UserEntity mockedUser = new UserEntity();
        mockedUser.setEmail("test@test.com");  // Setting email
        mockedUser.setUsed(new ArrayList<>());  // Initialize "used" list
        mockedUser.setAppList(new ArrayList<>());  // Initialize "appList" list

        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(mockedUser));
    }

    @Test
    @DisplayName("Edge case: Returns empty list when no public apps are found")
    @WithMockUser(username = "test@test.com")
    void getAllPublicAppsReturnsEmptyList() {
        Application app1 = Application.builder()
                .isPublic(false)
                .name("Test app")
                .build();

        appRepo.save(app1);

        Page<Application> result = appService.getPublicApps(Optional.empty(), 0, 3);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Edge case: Should throw error when category is provided but doesn't exist")
    @WithMockUser(username = "test@test.com")
    void getAllPublicAppsWithNonExistingCategoryThrowsError() {
        Category category = Category.builder()
                .name("Test")
                .build();

        categoryRepo.save(category);

        Application app1 = Application.builder()
                .isPublic(true)
                .name("Test app")
                .category(category)
                .build();

        appRepo.save(app1);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            appService.getPublicApps(Optional.of("Test category"), 0, 3);
        });

        assertEquals("Category not found", exception.getMessage());
    }
}
