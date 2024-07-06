package com.project.note.service.Interface;

import com.project.note.dto.UserDto;
import com.project.note.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface UserService {
    void registerUser(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsername(String name);

    User getCurrentUser();

    void updateUser(User user);

    void saveUser(User user);

    boolean isEmailUnique(String email, Long userId);

    String forgotPasswordUseEmail(String email) throws Exception;

    String resetPasswordUseEmail(String token, String newPassword) throws Exception;

    void save(User user);

    Map<String, String> generateTwoFactorAuthentication(User user, HttpServletRequest request);

    String enableTwoFactorAuthentication(User user, String totpCode, HttpServletRequest httpServletRequest);

    String disableTwoFactorAuthentication(User user);

    User getUserById(Long id);

    void saveAvatar(Long id, MultipartFile file);

    void deleteAvatar(Long id);

    List<User> getAllUsers();

    List<UserDto> searchUsers(String searchType, String keyword);

    List<UserDto> searchUsersById(Long id);

    List<UserDto> searchUsersByUsername(String username);

    List<UserDto> searchUsersByFullName(String fullName);

    List<UserDto> searchUsersByEmail(String email);

    void deleteUserById(Long id);

    User findById(Long id);

    boolean verifyTwoFactorAuthenticationCode(User user, String totpCode);

    void updateUserDetails(Long id, User user);

    void updateUserRole(String username, String newRole);
}
