package com.example.diansproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletComponentScan;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ServletComponentScan
@EnableJpaRepositories(basePackages = "com.example.diansproject.repository")
@EntityScan("com.example.diansproject.model")
public class DiansProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiansProjectApplication.class, args);
    }

}
