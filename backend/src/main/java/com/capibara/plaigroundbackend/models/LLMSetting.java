package com.capibara.plaigroundbackend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LLMSettings")
public class LLMSetting {
    @Id
    @GeneratedValue
    private Long id;
    @Builder.Default
    private String chatModel = "gpt-3.5-turbo";
    @Builder.Default
    private boolean defaultModel = false;
    Double temperature;
    @Column(length = 65535)
    String systemMessage;
}
