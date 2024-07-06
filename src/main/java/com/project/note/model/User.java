package com.project.note.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Column(name = "danhxung", nullable = false)
    private String title;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "pw", nullable = false)
    private String password;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "active")
    private int active;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "registration_time", nullable = false)
    private LocalDateTime registrationTime;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Note> notes = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<GroupMember> groupMemberships;

    @OneToMany(mappedBy = "user")
    private List<GroupNote> groupNotes;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private List<Group> createdGroups;

    @Column(name = "totp_secret", nullable = true)
    private String totpSecret;

    @Transient
    private boolean twoFactorAuthenticationVerified;

    public User(String fullName, String title, String email, String username, String password, int active, String role, LocalDateTime registrationTime, List<Note> notes, List<GroupMember> groupMemberships, List<GroupNote> groupNotes, List<Group> createdGroups) {
        this.fullName = fullName;
        this.title = title;
        this.email = email;
        this.username = username;
        this.password = password;
        this.active = active;
        this.role = role;
        this.registrationTime = registrationTime;
        this.notes = notes;
        this.groupMemberships = groupMemberships;
        this.groupNotes = groupNotes;
        this.createdGroups = createdGroups;
    }
}
