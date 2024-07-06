package com.project.note.dto;

import com.project.note.model.Notification;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class NotificationDto {
    private Long id;
    private String title;
    private String content;
    private String createdAt;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    public static NotificationDto from(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setContent(notification.getContent());
        dto.setCreatedAt(notification.getCreatedAt().format(FORMATTER));
        return dto;
    }
}