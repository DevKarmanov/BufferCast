package com.karmanov.tools.clipboardcollector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.HashSet;

@Service
public class ClipBoardCollectorService{
    private static final Logger logger = LoggerFactory.getLogger(ClipBoardCollectorService.class);
    private String lastText = "";
    private final HashSet<String> mailStorage = new HashSet<>();
    private final HashSet<String> textStorage = new HashSet<>();
    private final HashSet<String> phoneStorage = new HashSet<>();
    private final HashSet<String> httpsStorage = new HashSet<>();
    private final HashSet<String> customProtocolStorage = new HashSet<>();
    private final HashSet<String> filePathStorage = new HashSet<>();

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
        String urlRegex = "^[a-zA-Z][a-zA-Z0-9+.-]*://.+$";
        String emailRegex = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        String phoneRegex = "^\\+?[0-9\\s()-]{7,}$";
        String filePathRegex = "^[A-Z]:\\\\(?:[^\\\\/:*?\"<>|\\r\\n]+\\\\)*[^\\\\/:*?\"<>|\\r\\n]*$";

        if (text.matches(urlRegex)){
            sortUrl(text.trim());
        }
        if (text.matches(emailRegex) && !mailStorage.contains(text)){
            mailStorage.add(text);
            System.out.println("Email added to MailStorage: " + text);
        }
        if (text.matches(phoneRegex) && !phoneStorage.contains(text)) {
            phoneStorage.add(text);
            System.out.println("Phone added to PhoneStorage: " + text);
        }
        if (text.matches(filePathRegex) && !filePathStorage.contains(text)) {
            sortFilePath(text);
        }
        else if (!textStorage.contains(text) && !text.matches(filePathRegex)
                && !text.matches(urlRegex) && !text.matches(emailRegex)
                && !text.matches(phoneRegex)) {
            textStorage.add(text);
            System.out.println("Text added to TextStorage: " + text);
        }
    }

    private void sortUrl (String url){
        String httpsUrlRegex = "^https?://.+$";
        String customProtocolRegex = "^(?!https?|ftp)([a-zA-Z][a-zA-Z0-9+.-]*)://.+$";
        String urlPathRegex = "^file:///([A-Z]:/[^<>:\\\"|?*\\r\\n]*)$";

        if (url.matches(httpsUrlRegex) && !httpsStorage.contains(url)){
            httpsStorage.add(url);
            System.out.println("HTTPS Url added to HttpsStorage: " + url);
        }
        if (url.matches(customProtocolRegex) && !customProtocolStorage.contains(url)){
            customProtocolStorage.add(url);
            System.out.println("Custom Protocol Url added to CustomProtocolStorage: " + url);
        }
        else if (url.matches(urlPathRegex) && !filePathStorage.contains(url)) {
            sortFilePath(url);
        }
    }

    private void sortFilePath (String filePath){
        filePathStorage.add(filePath);
        System.out.println("File path added to FilePathStorage: " + filePath);
    }
}