package com.karmanov.tools.clipboardcollector.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UrlFilter implements ClipboardTextFilterInterface{
    private static final Pattern URL_REGEX = Pattern.compile("^([a-zA-Z][a-zA-Z0-9+.-]*):(?://)?\\S+$");
    private final Map<String, Set<String>> protocolToUrl = new HashMap<>();

    @Override
    public boolean support(String text) {
        return URL_REGEX.matcher(text).matches();
    }

    @Override
    public void handle(String text) {
        Matcher matcher = URL_REGEX.matcher(text);
        boolean isExist = protocolToUrl.values().stream().anyMatch(e -> e.contains(text));

        if(matcher.matches() && !isExist) {
            String protocol = matcher.group(1);
            protocolToUrl
                    .computeIfAbsent(protocol, p -> {
                        System.out.println("A new storage was created for url with a protocol: " + p);
                        return new HashSet<>();
                    })
                    .add(text);

            System.out.println("Url " + text + " has been added to storage: " + protocol);
        }
    }
}
