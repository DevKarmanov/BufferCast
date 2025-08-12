package com.karmanov.storage.repo.postgres;

import com.karmanov.storage.model.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface PostgresTextRepository extends JpaRepository<TextEntity, UUID> {}
