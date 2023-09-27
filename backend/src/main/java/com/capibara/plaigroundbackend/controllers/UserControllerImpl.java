package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.dtos.AddPublicApplicationDTO;
import com.capibara.plaigroundbackend.models.dtos.ApplicationDto;
import com.capibara.plaigroundbackend.models.dtos.ChangePasswordDTO;
import com.capibara.plaigroundbackend.models.dtos.PasswordChangedDTO;
import com.capibara.plaigroundbackend.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<?> getAllUserUsedApplications() {
        return ResponseEntity.ok(userService.getAllUsedApps().stream().map(ApplicationDto::new));
    }

    @Override
    public ResponseEntity<?> addAppToUsedApps(@RequestBody AddPublicApplicationDTO addPublicApplicationDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        userService.addAppToUsed(addPublicApplicationDTO.getId(), userEmail);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        userService.changePassword(userEmail, changePasswordDTO);
        return ResponseEntity.ok(new PasswordChangedDTO("Password changed"));
    }
}
