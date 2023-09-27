package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.AccessForbiddenException;
import com.capibara.plaigroundbackend.exceptions.InvalidDataException;
import com.capibara.plaigroundbackend.models.Application;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.models.dtos.ChangePasswordDTO;
import com.capibara.plaigroundbackend.repositories.ApplicationRepository;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceIntTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationRepository appRepo;

    @Autowired
    private UserEntityRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void cleanUp() {
        appRepo.deleteAll();
        userRepo.deleteAll();
    }

    @Test
    @DisplayName("Should return list of Apps used by user")
    @WithMockUser(username = "test@test.com")
    public void getUsedApps() {
        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .build();

        UserEntity owner = UserEntity.builder()
                .email("owner@app.com")
                .password(passwordEncoder.encode("password"))
                .build();

        Application app = Application.builder()
                .name("app")
                .owner(owner)
                .isPublic(true)
                .users((List.of(user)))
                .build();

        user.setUsed(List.of(app));

        appRepo.save(app);
        userRepo.save(user);
        userRepo.save(owner);

        List<Application> result = userService.getAllUsedApps();
        assertEquals(app.getName(), result.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list of Apps used by user when user has no apps")
    @WithMockUser(username = "test@test.com")
    public void getUsedAppsWhenNoAppsUsed() {
        UserEntity owner = UserEntity.builder()
                .email("owner@app.com")
                .password(passwordEncoder.encode("password"))
                .build();

        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .used(Collections.emptyList())
                .build();

        Application app = Application.builder()
                .name("app")
                .owner(owner)
                .isPublic(true)
                .users(Collections.emptyList())
                .build();

        appRepo.save(app);
        userRepo.save(user);
        userRepo.save(owner);

        List<Application> result = userService.getAllUsedApps();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should add public app to user's used apps list")
    @WithMockUser(username = "test@test.com")
    public void addAppToUsed_Success() {
        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password"))
                .used(new ArrayList<>())
                .build();

        userRepo.save(user);

        Application app = Application.builder()
                .name("app")
                .isPublic(true)
                .users(new ArrayList<>())
                .build();

        appRepo.save(app);

        userService.addAppToUsed(app.getId(), user.getEmail());

        UserEntity updatedUser = userRepo.findById(user.getId()).orElse(null);
        Application updatedApp = appRepo.findById(app.getId()).orElse(null);

        assertNotNull(updatedUser);
        assertNotNull(updatedApp);
        assertTrue(updatedUser.getUsed().contains(updatedApp));
        assertTrue(updatedApp.getUsers().contains(updatedUser));
    }

    @Test
    @DisplayName("Should throw AccessForbiddenException when the application does not exist or is not public")
    @WithMockUser(username = "test@test.com")
    public void addAppToUsed_AppNotFoundOrNotPublic() {
        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .used(Collections.emptyList())
                .password(passwordEncoder.encode("password"))
                .build();

        userRepo.save(user);

        Application app = Application.builder()
                .name("app")
                .users(Collections.emptyList())
                .isPublic(false)
                .build();

        appRepo.save(app);

        assertThrows(AccessForbiddenException.class, () -> userService.addAppToUsed(app.getId(), user.getEmail()));

        UserEntity updatedUser = userRepo.findById(user.getId()).orElse(null);

        assertNotNull(updatedUser);
        assertTrue(updatedUser.getUsed().isEmpty());
    }

    @Test
    @DisplayName("Should change password")
    @WithMockUser(username = "user@user.com")
    public void changePassword() {
        UserEntity user = UserEntity.builder()
                .email("user@user.com")
                .password(passwordEncoder.encode("password"))
                .build();

        userRepo.save(user);

        ChangePasswordDTO changePass = ChangePasswordDTO.builder()
                .oldPassword("password")
                .newPassword("password1")
                .build();

        userService.changePassword("user@user.com", changePass);

        assertTrue(passwordEncoder.matches("password1", user.getPassword()));
    }

    @Test
    @DisplayName("Should throw exception when old password matches the new one")
    @WithMockUser(username = "user@user.com")
    public void changePasswordThrowsExceptionWhenPasswordsAreTheSame() {
        UserEntity user = UserEntity.builder()
                .email("user@user.com")
                .password(passwordEncoder.encode("password"))
                .build();

        userRepo.save(user);

        ChangePasswordDTO changePass = ChangePasswordDTO.builder()
                .oldPassword("password")
                .newPassword("password")
                .build();

        assertThrows(InvalidDataException.class, () -> userService.changePassword("user@user.com", changePass));
    }
}

