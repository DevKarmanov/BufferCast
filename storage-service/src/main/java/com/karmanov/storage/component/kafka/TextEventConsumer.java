package com.karmanov.storage.component.kafka;

import com.karmanov.storage.dto.ClipboardText;
import com.karmanov.storage.service.common.CommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TextEventConsumer {

    private final CommonService commonService;
    private static final Logger logger = LoggerFactory.getLogger(TextEventConsumer.class);

    public TextEventConsumer(CommonService commonService) {
        this.commonService = commonService;
    }

    @KafkaListener(topics = "clipboard-events", groupId = "storage-service")
    public void handleEvent(ClipboardText event) {
        try {
            commonService.save(event);
        } catch (Exception e) {
            logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }
}
