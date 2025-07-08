package com.karmanov.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class StorageApp {
    public static void main(String[] args) {
        SpringApplication.run(StorageApp.class, args);
    }
}
