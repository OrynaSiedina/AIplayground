package com.capibara.plaigroundbackend.models.dtos;

import com.capibara.plaigroundbackend.models.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private String name;

    public CategoryDTO(Category category) {
        this.name = category.getName();
    }
}
