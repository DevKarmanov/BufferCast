package com.karmanov.storage.repo.h2;

import com.karmanov.storage.model.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface H2TextRepository extends JpaRepository<TextEntity, UUID> {}