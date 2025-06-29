package com.karmanov.tools.clipboardcollector.component.saver;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("path")
public class PathSaver implements TextSaver{
    private static final Pattern PATH_REGEX = Pattern.compile("^([a-zA-Z]):\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$");
    private final Map<String, Set<String>> discToPath = new HashMap<>();

    @Override
    public void save(String text) {
        Matcher matcher = PATH_REGEX.matcher(text);
        boolean isExist = discToPath.values().stream().anyMatch(e -> e.contains(text));

        if(matcher.matches() && !isExist) {
            String disc = matcher.group(1);
            discToPath
                    .computeIfAbsent(disc, d -> {
                        System.out.println("A new storage was created for path with a disc: " + d);
                        return new HashSet<>();
                    })
                    .add(text);

            System.out.println("Path " + text + " has been added to disc: " + disc);
        }
    }
}
