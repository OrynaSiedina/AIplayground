package com.capibara.plaigroundbackend.models.dtos;

import com.capibara.plaigroundbackend.models.Application;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDto {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String prompt;
    private String owner;
    private boolean isPublic;
    private String html;
    private String css;
    private String javaScript;

    public ApplicationDto(Application app) {
        this.id = app.getId();
        this.name = app.getName();
        this.category = app.getCategory() == null ? "uncategorized" : app.getCategory().getName();
        this.description = app.getDescription();
        this.prompt = app.getPrompt();
        this.owner = app.getOwner().getNickname();
        this.isPublic = app.isPublic();
        this.html = app.getHtml();
        this.css = app.getCss();
        this.javaScript = app.getJavaScript();
    }
}
