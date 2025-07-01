package com.karmanov.tools.clipboardcollector.component.validator;

import com.karmanov.tools.clipboardcollector.component.saver.*;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class TextValidatorImpl implements TextValidator{
    private final DefaultSaver defaultSaver;
    private final Map<String, Pattern> patterns = new LinkedHashMap<>() {{
        put("email", Pattern.compile("^[\\w.-]+@([\\w.-]+\\.[a-zA-Z]{2,})$"));
        put("path", Pattern.compile("^([a-zA-Z]):\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$"));
        put("url", Pattern.compile("^([a-zA-Z][a-zA-Z0-9+.-]*):(?://)?\\S+$"));
    }};

    private final Map<String, TextSaver> savers;

    public TextValidatorImpl(DefaultSaver defaultSaver,
                             EmailSaver emailSaver,
                             UrlSaver urlSaver,
                             PathSaver pathSaver) {
        this.defaultSaver = defaultSaver;
        this.savers = Map.of(
                "email", emailSaver,
                "path", pathSaver,
                "url", urlSaver
        );
    }

    @Override
    public void validation(String text) {
        for (Map.Entry<String, Pattern> entry : patterns.entrySet()) {
            String type = entry.getKey();
            Pattern pattern = entry.getValue();

            if (pattern.matcher(text).matches()) {
                TextSaver saver = savers.get(type);
                if (saver != null) {
                    saver.save(text);
                } else {
                    System.err.println("Warning: No saver for " + type + ".");
                }
                return;
            }
        }
        defaultSaver.save(text);
    }
}
