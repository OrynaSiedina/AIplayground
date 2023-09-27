package com.capibara.plaigroundbackend.repositories;

import com.capibara.plaigroundbackend.models.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    List<Category> findAll();

    Optional<Category> findByName(String name);
}
