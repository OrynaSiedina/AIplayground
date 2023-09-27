package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.models.Application;
import com.capibara.plaigroundbackend.models.dtos.ChangePasswordDTO;

import java.util.List;

public interface UserService {
    List<Application> getAllUsedApps();

    void addAppToUsed(Long id, String userEmail);

    void changePassword(String userEmail, ChangePasswordDTO changePasswordDTO);
}

