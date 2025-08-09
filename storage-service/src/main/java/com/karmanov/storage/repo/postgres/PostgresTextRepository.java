package com.karmanov.storage.repo.postgres;

import com.karmanov.storage.model.TextEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostgresTextRepository extends JpaRepository<TextEntity, UUID> {}
