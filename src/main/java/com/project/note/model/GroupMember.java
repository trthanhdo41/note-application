package com.project.note.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_members")
@NoArgsConstructor
@Getter
@Setter
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime joinAt = LocalDateTime.now();

    @Column(name = "blocked")
    private boolean blocked;

    public GroupMember(Group group, User user) {
        this.group = group;
        this.user = user;
        this.joinAt = LocalDateTime.now();
    }
}
