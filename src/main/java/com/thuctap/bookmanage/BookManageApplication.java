package com.thuctap.bookmanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class BookManageApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookManageApplication.class, args);

    }

}
