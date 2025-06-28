package com.karmanov.tools.clipboardcollector.component;

public interface ClipboardTextFilterInterface {
    boolean support(String text);
    void handle(String text);
}
