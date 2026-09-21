package com.visualnotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VisualNotesApplication {
    public static void main(String[] args) {
        SpringApplication.run(VisualNotesApplication.class, args);
    }
}
