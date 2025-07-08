package com.karmanov.tools.clipboardcollector.component.validation;

import com.karmanov.tools.clipboardcollector.dto.ClipboardTextSavedEvent;
import com.karmanov.tools.clipboardcollector.enums.TextType;
import com.karmanov.tools.clipboardcollector.kafka.ClipboardKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class TextValidatorImpl implements TextValidator{
    private final ClipboardKafkaProducer clipboardKafkaProducer;
    private static final Logger logger = LoggerFactory.getLogger(TextValidatorImpl.class);
    private final Map<TextType, Pattern> patterns = new LinkedHashMap<>() {{
        put(TextType.EMAIL, Pattern.compile("^[\\w.-]+@([\\w.-]+\\.[a-zA-Z]{2,})$"));
        put(TextType.PATH, Pattern.compile("^([a-zA-Z]):\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$"));
        put(TextType.URL, Pattern.compile("^([a-zA-Z][a-zA-Z0-9+.-]*):(?://)?\\S+$"));
    }};

    public TextValidatorImpl(ClipboardKafkaProducer clipboardKafkaProducer) {
        this.clipboardKafkaProducer = clipboardKafkaProducer;
    }

    @Override
    public void validate(String text) {
        for (Map.Entry<TextType, Pattern> entry : patterns.entrySet()) {
            TextType type = entry.getKey();
            Pattern pattern = entry.getValue();

            if (pattern.matcher(text).matches()) {
                UUID id = UUID.randomUUID();
                OffsetDateTime date = OffsetDateTime.now();
                ClipboardTextSavedEvent event = new ClipboardTextSavedEvent(text, type, id, date);
                clipboardKafkaProducer.sendEvent(event);
                logger.info("Text has been sent with type {} and id {}, at {}", type.getDescription(), id, date);
                return;
            }
        }
        UUID id = UUID.randomUUID();
        OffsetDateTime date = OffsetDateTime.now();
        ClipboardTextSavedEvent defaultEvent = new ClipboardTextSavedEvent(text, TextType.DEFAULT, id, date);
        clipboardKafkaProducer.sendEvent(defaultEvent);
        logger.info("Text has been sent with type {} and id {}, at {}", TextType.DEFAULT.getDescription(), id, date);
    }
}
