package com.capibara.plaigroundbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
public class Application {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String description;

    @Column(length = 65535)
    private String prompt;
    private boolean isPublic;

    @Column(length = 65535)
    private String html;

    @Column(length = 65535)
    private String css;

    @Column(length = 65535)
    private String javaScript;

    @ManyToOne
    @JoinColumn(name = "LLMSetting_id")
    private LLMSetting LLMSetting;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private UserEntity owner;

    @ManyToMany
    @JoinTable(
            name = "application_user",
            joinColumns = @JoinColumn(name = "application_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserEntity> users;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
