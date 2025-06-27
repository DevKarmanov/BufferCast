package com.karmanov.tools.clipboardcollector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

@Service
public class ClipBoardCollectorService{
    private static final Logger logger = LoggerFactory.getLogger(ClipBoardCollectorService.class);
    private String lastText = "";
    public void getTextFromClipboard(){
        logger.info("Launching Clipboard monitoring...");

        while (!Thread.currentThread().isInterrupted()){
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

            try {
                if (clipboard != null && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    String currentText = (String) clipboard.getData(DataFlavor.stringFlavor);

                    if (!currentText.equals(lastText)) {
                        System.out.println("Clipboard contents: " + currentText);
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
}
