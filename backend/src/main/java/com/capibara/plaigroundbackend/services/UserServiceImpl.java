package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.*;
import com.capibara.plaigroundbackend.models.Application;
import com.capibara.plaigroundbackend.models.UserEntity;
import com.capibara.plaigroundbackend.models.dtos.ChangePasswordDTO;
import com.capibara.plaigroundbackend.repositories.ApplicationRepository;
import com.capibara.plaigroundbackend.repositories.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserEntityRepository userRepo;
    private final ApplicationRepository appRepo;
    private final PasswordEncoder passwordEncoder;
    private final PrincipalService principalService;

    @Override
    public List<Application> getAllUsedApps() {
        String userEmail = principalService.getPrincipalName();
        UserEntity user = userRepo.findByEmail(userEmail).orElseThrow(() -> new NotFoundException("User not found"));
        return user.getUsed();
    }

    @Override
    public void addAppToUsed(Long id, String userEmail) {
        Optional<UserEntity> optionalUser = userRepo.findByEmail(userEmail);
        Optional<Application> optionalApp = appRepo.findById(id);

        if (optionalApp.isEmpty() || !optionalApp.get().isPublic()) {
            throw new AccessForbiddenException("Application does not exist or is not public.");
        }

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User not found");
        }

        Application app = optionalApp.get();
        UserEntity user = optionalUser.get();

        List<Application> usedApps = user.getUsed();

        if (usedApps.contains(app)) {
            throw new AlreadyInListException("Application already added", HttpStatusCode.valueOf(400));
        }

        usedApps.add(app);
        user.setUsed(usedApps);

        List<UserEntity> appUsers = app.getUsers();
        appUsers.add(user);
        app.setUsers(appUsers);

        appRepo.save(app);
        userRepo.save(user);
    }

    @Override
    public void changePassword(String userEmail, ChangePasswordDTO changePasswordDTO) {
        Optional<UserEntity> optionalUser = userRepo.findByEmail(userEmail);
        String newPassword = changePasswordDTO.getNewPassword();
        String oldPassword = changePasswordDTO.getOldPassword();

        if (oldPassword.equals(newPassword)) {
            throw new InvalidDataException("Old password matches the new one");
        }

        if (newPassword == null || ("").equals(newPassword)) {
            throw new DataRequiredException("New password");
        }

        if (oldPassword == null || ("").equals(oldPassword)) {
            throw new DataRequiredException("Old password");
        }

        if (optionalUser.isEmpty()) {
            throw new NotFoundException("User");
        }

        UserEntity user = optionalUser.get();

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);
        } else {
            throw new UnauthorisedException();
        }
    }
}
