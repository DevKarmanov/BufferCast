package com.karmanov.tools.clipboardcollector.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.karmanov.tools.clipboardcollector.dto.ClipboardTextSavedEvent;
import com.karmanov.tools.clipboardcollector.enums.TextType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClipboardKafkaProducerUnitTest {
    private ClipboardKafkaProducer clipboardKafkaProducer;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @BeforeEach
    void setUp() {
        clipboardKafkaProducer = new ClipboardKafkaProducer(kafkaTemplate);
        clipboardKafkaProducer.mapper = objectMapper;
    }

    private ClipboardTextSavedEvent addClipboardTextSavedEvent() {
        String text = "test-text";
        TextType type = TextType.DEFAULT;
        UUID id = UUID.randomUUID();
        OffsetDateTime date = OffsetDateTime.now();
        return new ClipboardTextSavedEvent(text, type, id, date);
    }

    @Test
    void sendEvent_whenSerializationSucceeds_shouldSendMessageToKafka() throws Exception {
        ClipboardTextSavedEvent event = addClipboardTextSavedEvent();
        byte[] serializedBytes = "serialized".getBytes();

        when(objectMapper.writeValueAsBytes(any(ClipboardTextSavedEvent.class))).thenReturn(serializedBytes);

        clipboardKafkaProducer.sendEvent(event);

        verify(kafkaTemplate).send("clipboard-events", serializedBytes);
    }

    @Test
    void sendEvent_whenSerializationFails_shouldNotSendAndLogError() throws JsonProcessingException {
        ClipboardTextSavedEvent event = addClipboardTextSavedEvent();

        when(objectMapper.writeValueAsBytes(any(ClipboardTextSavedEvent.class)))
                .thenThrow(new JsonProcessingException("Test exception") {});

        clipboardKafkaProducer.sendEvent(event);

        verify(kafkaTemplate, never()).send(any(), any());
    }

}
