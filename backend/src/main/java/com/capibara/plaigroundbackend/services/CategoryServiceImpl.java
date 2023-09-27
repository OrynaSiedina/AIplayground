package com.capibara.plaigroundbackend.services;

import com.capibara.plaigroundbackend.exceptions.ServerException;
import com.capibara.plaigroundbackend.models.Category;
import com.capibara.plaigroundbackend.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepo;

    @Override
    public List<Category> getAll() {
        try {
            return categoryRepo.findAll();
        } catch (Exception e) {
            throw new ServerException("Loading from database failed");
        }

    }
}
