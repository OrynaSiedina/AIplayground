package com.capibara.plaigroundbackend.models.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestForNewAppDto {
    private String name;
    private String description;
    private String prompt;
}
