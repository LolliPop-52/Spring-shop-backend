package com.example.spring_shop;



import com.example.spring_shop.test.ProductCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.Set;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
@EnableAsync
public class SpringShopApplication {

    private final ProductCreator productCreator;

    public static void main(String[] args) {

        SpringApplication.run(SpringShopApplication.class, args);

    }
}
