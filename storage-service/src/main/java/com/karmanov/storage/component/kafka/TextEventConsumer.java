package com.karmanov.storage.component.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karmanov.storage.dto.StorageTextSavedEvent;
import com.karmanov.storage.service.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TextEventConsumer {
    @Autowired
    private ObjectMapper mapper;
    private final StorageService storageService;
    private static final Logger logger = LoggerFactory.getLogger(TextEventConsumer.class);

    public TextEventConsumer(StorageService storageService) {
        this.storageService = storageService;
    }

    @KafkaListener(topics = "clipboard-events", groupId = "storage-service")
    public void handleEvent(byte[] bytesEvent) {
        try {
            StorageTextSavedEvent event = mapper.readValue(bytesEvent, StorageTextSavedEvent.class);
            storageService.saveToH2(event);
        } catch (Exception e) {
            logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }
}
