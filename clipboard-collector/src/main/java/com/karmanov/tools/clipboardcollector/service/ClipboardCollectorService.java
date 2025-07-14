package com.karmanov.tools.clipboardcollector.service;

import com.karmanov.tools.clipboardcollector.component.validation.TextValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

@Service
public class ClipboardCollectorService {
    private String lastText = "";
    private final TextValidator textValidator;
    private static final Logger logger = LoggerFactory.getLogger(ClipboardCollectorService.class);

    public ClipboardCollectorService(TextValidator textValidator) {
        this.textValidator = textValidator;
    }

    public void clipboardMonitoring() {
        logger.info("Launching Clipboard monitoring...");

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String initialText = "";

        try {
            if (clipboard != null && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                initialText = (String) clipboard.getData(DataFlavor.stringFlavor);
            }
        } catch (Exception e) {
            logger.warn("Could not read initial clipboard content: {} - {}", e.getClass().getName(), e.getMessage());
        }

        lastText = initialText;

        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (clipboard != null && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    String currentText = (String) clipboard.getData(DataFlavor.stringFlavor);
                    if (!currentText.equals(lastText)) {
                        logger.info("Clipboard text is: {}", currentText);
                        textValidator.validate(currentText);
                        lastText = currentText;
                    }
                }
            } catch (IllegalStateException e) {
                logger.warn("The clipboard is temporarily occupied. We'll try it later...");
            } catch (UnsupportedFlavorException | IOException e) {
                logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.info("Clipboard monitoring has been stopped.");
                Thread.currentThread().interrupt();
            }
        }
    }
}