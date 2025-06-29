package com.karmanov.tools.clipboardcollector.component.saver;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Component("default")
public class DefaultSaver implements TextSaver{
    private final Set<String> textStorage = new HashSet<>();
    @Override
    public void save(String text) {
        if(!textStorage.contains(text)){
            textStorage.add(text);
            System.out.println("Added text to TextStorage: " + text);
        }
    }
}
