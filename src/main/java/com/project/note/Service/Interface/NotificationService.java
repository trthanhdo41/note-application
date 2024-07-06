package com.project.note.Service.Interface;

import com.project.note.Model.Notification;
import java.util.List;

public interface NotificationService {
    Notification saveNotification(Notification notification);
    List<Notification> getAllNotifications();
    Notification getNotificationById(Long id);
    void deleteNotification(Long id);
    void save(Notification notification);
}