package com.karmanov.tools.clipboardcollector.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karmanov.tools.clipboardcollector.dto.ClipboardTextSavedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClipboardKafkaProducer {
    @Autowired
    private ObjectMapper mapper;
    private final KafkaTemplate<String, byte[]> kafkaTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ClipboardKafkaProducer.class);

    public ClipboardKafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(ClipboardTextSavedEvent event) {
        try {
            byte[] byteEvent = mapper.writeValueAsBytes(event);
            kafkaTemplate.send("clipboard-events", byteEvent);
        } catch (Exception e) {
            logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
        }
    }
}
