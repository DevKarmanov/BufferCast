package com.karmanov.tools.clipboarcollector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

@Service
public class ClipBoardCollectorService implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(ClipBoardCollectorService.class);
    public static void getTextFromClipboard(){
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        String lastText = "";

        while (true){
            try{
                if (clipboard != null && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    String currentText = (String) clipboard.getData(DataFlavor.stringFlavor);

                    if (!currentText.equals(lastText)) {
                        System.out.println("Clipboard contents: " + currentText);
                        lastText = currentText;
                    }
                }
            }
            catch(IllegalStateException e){
                System.out.println("The clipboard is temporarily occupied. We'll try it later...");
            } catch (UnsupportedFlavorException | IOException e) {
                logger.error("Error reading the file", e);
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    @Override
    public void run(String... args) {
        ClipBoardCollectorService.getTextFromClipboard();
    }
}
