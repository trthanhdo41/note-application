package com.project.note.dto;

import com.project.note.model.Note;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class NoteTrashDto {
    private Long id;
    private String title;
    private String content;
    private String createdAt;
    private String timeRemaining;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static NoteTrashDto from(Note note) {
        NoteTrashDto dto = new NoteTrashDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        dto.setCreatedAt(note.getTime().format(formatter));

        LocalDateTime deletionTime = note.getDeletedAt().plusDays(30);
        Duration duration = Duration.between(LocalDateTime.now(), deletionTime);
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        dto.setTimeRemaining(days + " ngày " + hours + " giờ " + minutes + " phút");

        return dto;
    }
}
