package com.pfe.dataops.dataopsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DataopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataopsApplication.class, args);
    }

}
