package com.karmanov.tools.clipboardcollector.utils.init;

import com.karmanov.tools.clipboardcollector.service.ClipBoardCollectorService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ClipBoardCollectorInitialize implements ApplicationListener<ApplicationReadyEvent> {
    private final ClipBoardCollectorService clipBoardCollectorService;

    public ClipBoardCollectorInitialize(ClipBoardCollectorService clipBoardCollectorService) {
        this.clipBoardCollectorService = clipBoardCollectorService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Thread watcherThread = new Thread(clipBoardCollectorService::getTextFromClipboard, "clipboard");
        watcherThread.setDaemon(false);
        watcherThread.start();
    }
}
