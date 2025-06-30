package com.broadenit.broadenit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BroadenItApplication {

    public static void main(String[] args) {
        SpringApplication.run(BroadenItApplication.class, args);
    }

}
