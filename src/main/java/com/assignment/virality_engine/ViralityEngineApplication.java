package com.assignment.virality_engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ViralityEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(ViralityEngineApplication.class, args);
    }

}
