package com.huce.webblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
public class WebBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebBlogApplication.class, args);
    }

}
