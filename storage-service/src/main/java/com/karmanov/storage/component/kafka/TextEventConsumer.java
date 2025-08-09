package com.karmanov.storage.component.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.service.common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TextEventConsumer {
    @Autowired
    private ObjectMapper mapper;
    private final CommonService commonService;
    private static final Logger logger = LoggerFactory.getLogger(TextEventConsumer.class);

    public TextEventConsumer(CommonService commonService) {
        this.commonService = commonService;
    }

    @KafkaListener(topics = "clipboard-events", groupId = "storage-service")
    public void handleEvent(byte[] bytesEvent) {
        try {
            ClipboardText event = mapper.readValue(bytesEvent, ClipboardText.class);
            commonService.save(event);
        } catch (Exception e) {
            logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }
}
