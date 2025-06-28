package com.karmanov.tools.clipboardcollector.component;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailFilter implements ClipboardTextFilterInterface {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w.-]+@([\\w.-]+\\.[a-zA-Z]{2,})$");
    private final Map<String, Set<String>> domainToEmails = new HashMap<>();

    @Override
    public boolean support(String text) {
        return EMAIL_REGEX.matcher(text).matches();
    }

    @Override
    public void handle(String text) {
        Matcher matcher = EMAIL_REGEX.matcher(text);
        boolean isExist = domainToEmails.values().stream().anyMatch(e -> e.contains(text));

        if (matcher.matches() && !isExist) {
            String domain = matcher.group(1);
            domainToEmails
                    .computeIfAbsent(domain, d ->{
                        System.out.println("A new storage was created for mail with a domain: " + d);
                        return new HashSet<>();
                    })
                    .add(text);

            System.out.println("Email " + text + " has been added to storage: " + domain);
        }
    }
}
