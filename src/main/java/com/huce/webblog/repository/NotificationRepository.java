package com.huce.webblog.repository;

import com.huce.webblog.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUidOrderByCreatedAtDesc(String uid);
}