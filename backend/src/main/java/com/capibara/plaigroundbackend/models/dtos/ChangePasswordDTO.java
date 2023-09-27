package com.capibara.plaigroundbackend.models.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
