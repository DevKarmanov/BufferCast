package com.karmanov.tools.clipboarcollector.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class ClipBoardCollectorService {
    public void getTextFromClipboard(){
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
                e.printStackTrace();
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
