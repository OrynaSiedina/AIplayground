package com.capibara.plaigroundbackend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PasswordChangedDTO {
    private String message;
}
