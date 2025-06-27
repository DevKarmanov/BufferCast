package com.karmanov.tools.clipboardcollector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;

@Service
public class ClipBoardCollectorService{
    private static final Logger logger = LoggerFactory.getLogger(ClipBoardCollectorService.class);
    private String lastText = "";
    private final HashSet<File> lastFiles = new HashSet<>();
    private final HashSet<String> textStorage = new HashSet<>();
    private final HashSet<File> imgStorage = new HashSet<>();
    private final HashSet<String> urlStorage = new HashSet<>();
    private final HashSet<File> fileStorage = new HashSet<>();

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
                if (clipboard != null && clipboard.isDataFlavorAvailable(DataFlavor.javaFileListFlavor)) {
                    @SuppressWarnings("unchecked")
                    List<File> currentFiles = (List<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
                    HashSet<File> currentFilesSet = new HashSet<>(currentFiles);

                    for (File currentFile : currentFilesSet) {
                        addFileToStorage(currentFile);

                        if (!currentFilesSet.equals(lastFiles)) {
                            lastFiles.clear();
                            lastFiles.add(currentFile);
                        }
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

    public void addTextToStorage(String text){
        String regex = "^[a-zA-Z][a-zA-Z0-9+.-]*://.+$";
        if (text.matches(regex) && !urlStorage.contains(text)){
            urlStorage.add(text);
            System.out.println("Url in the clipboard: " + text);
        }
        if (!text.matches(regex) && !textStorage.contains(text)) {
            textStorage.add(text);
            System.out.println("Text in the clipboard: " + text);
        }
    }

    public void addFileToStorage(File file){
        if (isImageFile(file) && !imgStorage.contains(file)){
            imgStorage.add(file);
            System.out.println("Img in the clipboard: " + file.getName());
        }
        else if (!fileStorage.contains(file) && !isImageFile(file)){
            fileStorage.add(file);
            System.out.println("File in the clipboard: " + file.getName());
        }
    }

    private boolean isImageFile(File file) {
        try {
            String mimeType = Files.probeContentType(file.toPath());
            return mimeType != null && mimeType.startsWith("image");
        } catch (IOException e) {
            logger.warn("Cannot determine file type for {}", file.getName());
            return false;
        }
    }
}