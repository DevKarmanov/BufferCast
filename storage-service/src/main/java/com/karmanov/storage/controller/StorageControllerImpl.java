package com.karmanov.storage.controller;

import com.karmanov.storage.model.TextEntity;
import com.karmanov.storage.service.common.CommonService;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
public class StorageControllerImpl implements StorageController {
    private final CommonService commonService;

    public StorageControllerImpl(CommonService commonService) {
        this.commonService = commonService;
    }

    @Override
    public ResponseEntity<String> deleteById(UUID id) {
        try {
            commonService.deleteById(id);
            return ResponseEntity.ok("The object was successfully deleted: " + id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Deletion is not possible: " + e.getMessage());
        } catch (DataAccessException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database access error during deletion: " + id);
        }
    }

    @Override
    public ResponseEntity<?> findById(UUID id) {
        try {
            Optional<TextEntity> optionalText = commonService.findById(id);
            if (optionalText.isPresent()) {
                return ResponseEntity.ok(optionalText.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Object with ID " + id + " not found");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid ID: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error in the search: " + e.getMessage());
        }
    }
}
