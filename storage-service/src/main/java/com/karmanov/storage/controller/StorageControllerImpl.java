package com.karmanov.storage.controller;

import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.service.common.CommonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class StorageControllerImpl implements StorageController {
    private final CommonService commonService;

    public StorageControllerImpl(CommonService commonService) {
        this.commonService = commonService;
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        commonService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<TextEntity> findById(UUID id) {
        commonService.findById(id);
        return ResponseEntity.ok().build();
    }
}
