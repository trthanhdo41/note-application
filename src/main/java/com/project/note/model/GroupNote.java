package com.project.note.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_notes")
@NoArgsConstructor
@Getter
@Setter
public class GroupNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String noteTitle;

    @Column(columnDefinition = "TEXT")
    private String noteContent;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    public GroupNote(Group group, User user, String noteTitle, String noteContent) {
        this.group = group;
        this.user = user;
        this.noteTitle = noteTitle;
        this.noteContent = noteContent;
        this.createdAt = LocalDateTime.now();
    }
}
