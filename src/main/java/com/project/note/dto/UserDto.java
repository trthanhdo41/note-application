package com.project.note.dto;

import com.project.note.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String fullName;
    private String title;
    private String email;
    private String username;
    private String password;
    private String role;
    private String registrationTime;
    private String avatar;
    private String groupRole;
    private String active;
    private String totpStatus;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    public static UserDto from(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setTitle(convertTitle(user.getTitle()));
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setRole(convertRole(user.getRole()));
        dto.setRegistrationTime(user.getRegistrationTime().format(FORMATTER));
        dto.setAvatar(user.getAvatar());
        dto.setActive(convertActive(user.getActive()));
        dto.setTotpStatus(convertTotpStatus(user.getTotpSecret()));
        return dto;
    }

    private static String convertTitle(String title) {
        if ("Ms".equals(title)) {
            return "Female";
        } else if ("Mr".equals(title)) {
            return "Male";
        } else {
            return "Other";
        }
    }

    private static String convertRole(String role) {
        switch (role) {
            case "ROLE_USER":
                return "Member";
            case "ROLE_ADMIN":
                return "Admin";
            case "ROLE_MANAGER":
                return "Manager";
            case "ROLE_VIPMEMBER":
                return "VIPMember";
            default:
                return role;
        }
    }

    private static String convertActive(int active) {
        return active == 1 ? "<span style='color: green;'>✓</span>" : "<span style='color: red;'>✗</span>";
    }

    private static String convertTotpStatus(String totpSecret) {
        return (totpSecret != null && !totpSecret.isEmpty()) ? "<span style='color: green;'>✓</span>" : "<span style='color: red;'>✗</span>";
    }

}
