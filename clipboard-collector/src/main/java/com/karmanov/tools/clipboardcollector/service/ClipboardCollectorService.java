package com.karmanov.tools.clipboardcollector.service;

import com.karmanov.tools.clipboardcollector.component.ClipboardTextFilterInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ClipboardCollectorService {
    private final List<ClipboardTextFilterInterface> textFilters;
    private String lastText = "";
    private final Set<String> textStorage = new HashSet<>();
    private static final Logger logger = LoggerFactory.getLogger(ClipboardCollectorService.class);

    public ClipboardCollectorService(List<ClipboardTextFilterInterface> textFilters) {
        this.textFilters = textFilters;
    }

    public void clipboardMonitoring(){
        logger.info("Launching Clipboard monitoring...");

        while (!Thread.currentThread().isInterrupted()){
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            try {
                if (clipboard != null && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    String currentText = (String) clipboard.getData(DataFlavor.stringFlavor);
                    addTextToStorage(currentText);

                    if (!currentText.equals(lastText)) {
                        lastText = currentText;
                    }
                }
            } catch (IllegalStateException e) {
                logger.warn("The clipboard is temporarily occupied. We'll try it later...");
            } catch (UnsupportedFlavorException | IOException e) {
                logger.error("Error: {} - {}", e.getClass().getName(), e.getMessage(), e);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.info("Clipboard monitoring has been stopped.");
                Thread.currentThread().interrupt();
            }
        }
    }

    private void addTextToStorage(String text){
        boolean handled = false;

        for (ClipboardTextFilterInterface textFilter : textFilters){
            if (textFilter.support(text)){
                textFilter.handle(text);
                handled = true;
                break;
            }
        }

        if (!handled && !textStorage.contains(text)){
            textStorage.add(text);
            System.out.println("Added text to TextStorage: " + text);
        }
    }
}