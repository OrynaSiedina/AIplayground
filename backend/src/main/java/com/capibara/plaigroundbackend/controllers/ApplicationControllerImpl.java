package com.capibara.plaigroundbackend.controllers;

import com.capibara.plaigroundbackend.models.Application;
import com.capibara.plaigroundbackend.models.dtos.ApplicationDto;
import com.capibara.plaigroundbackend.models.dtos.RequestForNewAppDto;
import com.capibara.plaigroundbackend.models.dtos.UpdateAppDTO;
import com.capibara.plaigroundbackend.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApplicationControllerImpl implements ApplicationController {

    private final ApplicationService appService;

    @Override
    public ResponseEntity<?> createApp(@RequestBody RequestForNewAppDto request) {
        Application app = appService.create(request);
        return ResponseEntity.status(200).body(new ApplicationDto(app));
    }

    @Override
    public ResponseEntity<?> getAppById(@PathVariable Long id) {
        return ResponseEntity.ok(new ApplicationDto(appService.getById(id)));
    }

    @Override
    public ResponseEntity<?> updateApp(@RequestBody UpdateAppDTO updateAppDTO, @PathVariable Long id) {
        return ResponseEntity.status(200).body(new ApplicationDto(appService.update(updateAppDTO, id)));
    }

    @Override
    public ResponseEntity<?> getUsersApps() {
        return ResponseEntity.status(200).body(appService.getUsersApps());
    }

    @Override
    public ResponseEntity<?> deleteApp(Long id) {
        return ResponseEntity.status(200).body(new ApplicationDto(appService.delete(id)));
    }
}
