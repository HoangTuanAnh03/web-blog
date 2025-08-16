package com.huce.webblog.service;


import com.huce.webblog.dto.NotificationEvent;

public interface EmailService
{
    void sendEmailRegister(NotificationEvent message);

    void sendEmailForgotPassword(NotificationEvent message);
}
