package com.karmanov.tools.clipboarcollector;

import com.karmanov.tools.clipboarcollector.service.ClipBoardCollectorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class,args);
        new ClipBoardCollectorService().getTextFromClipboard();
    }
}
