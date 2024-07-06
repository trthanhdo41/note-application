package com.project.note.service.Interface;

import com.project.note.model.Notification;
import java.util.List;

public interface NotificationService {
    Notification saveNotification(Notification notification);
    List<Notification> getAllNotifications();
    Notification getNotificationById(Long id);
    void deleteNotification(Long id);
    void save(Notification notification);
}