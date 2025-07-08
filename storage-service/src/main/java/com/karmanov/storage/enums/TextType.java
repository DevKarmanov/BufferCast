package com.karmanov.storage.enums;

import lombok.Getter;

@Getter
public enum TextType {
    EMAIL("Email"),
    URL("URL"),
    PATH("File path"),
    DEFAULT("Text");

    private final String description;

    TextType(String description) {
        this.description = description;
    }
}
