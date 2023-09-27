package com.capibara.plaigroundbackend.models.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateAppDTO {
    private String name;
    private String description;
    private boolean isPublic;
    private String category;
}
