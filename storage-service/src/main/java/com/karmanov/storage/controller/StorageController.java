package com.karmanov.storage.controller;

import com.karmanov.storage.model.TextEntity;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping("/storage")
public interface StorageController {
    @PostMapping("/{id}")
    ResponseEntity<Void> deleteById(@PathVariable UUID id);

    @GetMapping("/{id}")
    ResponseEntity<TextEntity> findById(@PathVariable UUID id);

}
