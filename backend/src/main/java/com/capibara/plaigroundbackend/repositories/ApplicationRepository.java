package com.capibara.plaigroundbackend.repositories;

import com.capibara.plaigroundbackend.models.Application;
import com.capibara.plaigroundbackend.models.Category;
import com.capibara.plaigroundbackend.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findAll();

    List<Application> findAllByOwner(UserEntity owner);

    Page<Application> findAllByIsPublicTrueAndCategoryNotNullAndIdNotIn(List<Long> excludedIds, Pageable pageable);

    Page<Application> findAllByIsPublicTrueAndCategoryAndIdNotIn(Category category, List<Long> excludedIds, Pageable pageable);

}
