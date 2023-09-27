package com.capibara.plaigroundbackend.models.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterDTO {
    private String email;
    private String nickname;
}
