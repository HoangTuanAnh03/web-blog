package com.huce.webblog.service;

import com.huce.webblog.dto.response.NotificationResponse;

import java.util.List;

public interface INotificationService {
    public List<NotificationResponse> getNotification(String uid);
}
