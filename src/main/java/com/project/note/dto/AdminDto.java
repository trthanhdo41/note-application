package com.project.note.dto;

import com.project.note.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class AdminDto {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private String registrationTime;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    public static AdminDto from(User user) {
        AdminDto dto = new AdminDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole((user.getRole()));
        dto.setRegistrationTime(user.getRegistrationTime().format(FORMATTER));
        return dto;
    }

    public String getFirstName() {
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.split(" ");
            return parts[parts.length - 1];
        }
        return "";
    }
}
