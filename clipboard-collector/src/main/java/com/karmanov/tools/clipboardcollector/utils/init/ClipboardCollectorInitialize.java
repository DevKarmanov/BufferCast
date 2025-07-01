package com.karmanov.tools.clipboardcollector.utils.init;

import com.karmanov.tools.clipboardcollector.service.ClipboardCollectorService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ClipboardCollectorInitialize implements ApplicationListener<ApplicationReadyEvent> {
    private final ClipboardCollectorService clipBoardCollectorService;

    public ClipboardCollectorInitialize(ClipboardCollectorService clipBoardCollectorService) {
        this.clipBoardCollectorService = clipBoardCollectorService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Thread watcherThread = new Thread(clipBoardCollectorService::clipboardMonitoring, "clipboard");
        watcherThread.setDaemon(false);
        watcherThread.start();
    }
}
