package com.capibara.plaigroundbackend.repositories;

import com.capibara.plaigroundbackend.models.LLMSetting;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LLMSettingRepository extends JpaRepository<LLMSetting, Long> {
    Optional<LLMSetting> findByDefaultModelTrue();

    Optional<LLMSetting> findById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE LLMSetting ls SET ls.defaultModel = false")
    void setAllDefaultFalse();

}
