package com.huce.webblog.controller;

import com.huce.webblog.advice.exception.BadRequestException;
import com.huce.webblog.dto.ApiResponse;
import com.huce.webblog.dto.response.NotificationResponse;
import com.huce.webblog.service.INotificationService;
import com.huce.webblog.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/blog/notification")
@AllArgsConstructor
public class NotificationController {
    INotificationService notificationService;
    UserService userService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotification() {
        String uid = userService.fetchMyInfo().getId();
        if (uid == null) {
            throw new BadRequestException("UID not found");
        }

        ApiResponse<List<NotificationResponse>> apiResponse = ApiResponse.<List<NotificationResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Success")
                .data(notificationService.getNotification(uid))
                .build();
        return ResponseEntity.ok()
                .body(apiResponse);
    }
}
