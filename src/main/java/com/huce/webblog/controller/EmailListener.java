package com.huce.webblog.controller;


import com.huce.webblog.dto.NotificationEvent;
import com.huce.webblog.service.EmailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailListener {
    EmailService emailService;

    @KafkaListener(topics = "email_register")
    public void listenNotificationDelivery(NotificationEvent message){
        log.info("Message received: {}", message);
        emailService.sendEmailRegister(message);
    }

    @KafkaListener(topics = "email_forgot_password")
    public void listenNotificationForgotPassword(NotificationEvent message){
        log.info("Message received forgot-password: {}", message);
        emailService.sendEmailForgotPassword(message);
    }
}
