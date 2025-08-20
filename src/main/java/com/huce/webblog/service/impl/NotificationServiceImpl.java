package com.huce.webblog.service.impl;

import com.huce.webblog.dto.mapper.NotificationMapper;
import com.huce.webblog.dto.response.NotificationResponse;
import com.huce.webblog.repository.NotificationRepository;
import com.huce.webblog.service.INotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationServiceImpl implements INotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    @Override
    public List<NotificationResponse> getNotification(String uid) {
        return notificationRepository.findAllByUidOrderByCreatedAtDesc(uid).stream().map(notificationMapper::toDto).toList();
    }
}
