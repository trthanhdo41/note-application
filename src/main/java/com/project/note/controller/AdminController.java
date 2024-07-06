package com.project.note.controller;

import com.project.note.dto.AdminDto;
import com.project.note.dto.NotificationDto;
import com.project.note.dto.UserDto;
import com.project.note.model.Notification;
import com.project.note.model.User;
import com.project.note.service.EmailService;
import com.project.note.service.Interface.AdminService;
import com.project.note.service.Interface.GroupService;
import com.project.note.service.Interface.NotificationService;
import com.project.note.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private NotificationService notificationService;

    private boolean checkUserRole(UserService userService, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("message", "Your role has changed. Please log in again.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return false;
        }
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = userService.findByUsername(username);
        if (user == null || (!user.getRole().equals("ROLE_ADMIN") && !user.getRole().equals("ROLE_MANAGER"))) {
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("message", "Your role has changed. Please log in again.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return false;
        }
        return true;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String adminDashboard(Model model, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        AdminDto adminDto = adminService.getAdminByUsername(username);
        List<UserDto> users = userService.getAllUsers().stream().map(UserDto::from).collect(Collectors.toList());
        List<NotificationDto> notifications = notificationService.getAllNotifications().stream().map(NotificationDto::from).collect(Collectors.toList());
        model.addAttribute("adminInfo", adminDto);
        model.addAttribute("users", users);
        model.addAttribute("notifications", notifications);
        return "admin/index";
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchAccounts(@RequestParam String searchType, @RequestParam String keyword, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return ResponseEntity.status(403).build();
        }
        List<UserDto> users = userService.searchUsers(searchType, keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("users", users);
        if (users.isEmpty()) {
            response.put("message", "Không tìm thấy tài khoản có " + searchType + " là: " + keyword);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ResponseBody
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return ResponseEntity.status(403).build();
        }
        UserDto user = UserDto.from(userService.getUserById(id));
        return ResponseEntity.ok(user);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deleteUserById(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String currentUsername = userDetails.getUsername();

        if (userService.findByUsername(currentUsername).getId().equals(id)) {
            redirectAttributes.addFlashAttribute("message", "You cannot delete yourself. Bạn không thể xóa chính mình");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin";
        }

        User userToDelete = userService.findById(id);

        if (authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_MANAGER"))) {
            if (userToDelete.getRole().contains("ROLE_ADMIN") || userToDelete.getRole().contains("ROLE_MANAGER")) {
                redirectAttributes.addFlashAttribute("message", "Managers cannot delete other Admins or Managers. Manager không thể xóa Admin hoặc Manager khác");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return "redirect:/admin";
            }
        }

        try {
            userService.deleteUserById(id);

            String subject = "Thông báo xóa tài khoản Note myself";
            String text = "Xin chào " + userToDelete.getFullName() + ",\n" +
                    "Tài khoản " + userToDelete.getUsername() + " của bạn đã bị xóa.";
            emailService.sendEmail(userToDelete.getEmail(), subject, text);

            redirectAttributes.addFlashAttribute("message", "User deleted successfully. Xóa người dùng thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Failed to delete user. Xóa người dùng thất bại(Có thể do người dùng đang trong quá trình kích hoạt tài khoản)");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String editUserForm(@PathVariable Long id, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }

        User currentUser = userService.findByUsername(authentication.getName());
        User user = userService.findById(id);

        if (isUserAllowedToEdit(currentUser, user, redirectAttributes)) {
            model.addAttribute("user", user);
            model.addAttribute("currentUserRole", currentUser.getRole());
            return "admin/edit-user";
        }
        return "redirect:/admin";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String updateUser(@PathVariable Long id, @ModelAttribute("user") User user, RedirectAttributes redirectAttributes, Authentication authentication) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }

        User currentUser = userService.findByUsername(authentication.getName());
        User existingUser = userService.findById(id);

        if (!isUserAllowedToEdit(currentUser, existingUser, redirectAttributes)) {
            return "redirect:/admin";
        }

        if (!existingUser.getUsername().equals(user.getUsername()) && userService.existsByUsername(user.getUsername())) {
            redirectAttributes.addFlashAttribute("message", "Tên tài khoản đã tồn tại");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/edit/" + id;
        }

        if (!existingUser.getEmail().equals(user.getEmail()) && userService.existsByEmail(user.getEmail())) {
            redirectAttributes.addFlashAttribute("message", "Email đã tồn tại");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/edit/" + id;
        }

        if (user.getTotpSecret() != null && user.getTotpSecret().isEmpty()) {
            user.setTotpSecret(null);
        }

        if (currentUser.getRole().equals("ROLE_MANAGER") && user.getRole().equals("ROLE_MANAGER")) {
            redirectAttributes.addFlashAttribute("message", "Manager không thể chỉnh sửa vai trò thành MANAGER.");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            return "redirect:/admin/edit/" + id;
        }

        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Xin chào ").append(user.getFullName()).append(",\n");
        emailContent.append("Cập nhật tài khoản ").append(user.getUsername()).append(" của bạn:\n");

        if (!existingUser.getUsername().equals(user.getUsername())) {
            emailContent.append("Username được cập nhật từ ").append(existingUser.getUsername()).append(" sang ").append(user.getUsername()).append(".\n");
        }

        if (!existingUser.getFullName().equals(user.getFullName())) {
            emailContent.append("Full Name được cập nhật từ ").append(existingUser.getFullName()).append(" sang ").append(user.getFullName()).append(".\n");
        }

        if (!existingUser.getEmail().equals(user.getEmail())) {
            emailContent.append("Email được cập nhật từ ").append(existingUser.getEmail()).append(" sang ").append(user.getEmail()).append(".\n");
        }

        if (!existingUser.getTitle().equals(user.getTitle())) {
            emailContent.append("Chức danh được cập nhật từ ").append(existingUser.getTitle()).append(" sang ").append(user.getTitle()).append(".\n");
        }

        if (!existingUser.getRole().equals("ROLE_" + user.getRole())) {
            switch (user.getRole()) {
                case "VIPMEMBER":
                    emailContent.append("Chúc mừng bạn đã được nâng cấp lên VIPMEMBER.\n");
                    break;
                case "MANAGER":
                    emailContent.append("Chúc mừng bạn đã được nâng cấp lên MANAGER.\n");
                    break;
                default:
                    emailContent.append("Tài khoản của bạn đã quay về trạng thái thành viên thường.\n");
                    break;
            }
        }

        if (existingUser.getActive() != user.getActive()) {
            if (user.getActive() == 1) {
                emailContent.append("Tài khoản của bạn đã được kích hoạt.\n");
            } else {
                emailContent.append("Tài khoản của bạn đã bị vô hiệu hóa.\n");
            }
        }

        userService.updateUserDetails(id, user);

        emailService.sendEmail(user.getEmail(), "Cập nhật thông tin tài khoản Note Myself", emailContent.toString());

        redirectAttributes.addFlashAttribute("message", "User updated successfully. Cập nhật người dùng thành công");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/admin";
    }

    @GetMapping("/check-username-email")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @ResponseBody
    public Map<String, Boolean> checkUsernameEmail(@RequestParam String username, @RequestParam String email, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return new HashMap<>();
        }
        Map<String, Boolean> response = new HashMap<>();
        response.put("usernameExists", userService.existsByUsername(username));
        response.put("emailExists", userService.existsByEmail(email));
        return response;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    private boolean isUserAllowedToEdit(User currentUser, User targetUser, RedirectAttributes redirectAttributes) {
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            if (currentUser.getId().equals(targetUser.getId())) {
                redirectAttributes.addFlashAttribute("message", "Admin không thể chỉnh sửa chính mình.");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return false;
            }
        } else if (currentUser.getRole().equals("ROLE_MANAGER")) {
            if (currentUser.getId().equals(targetUser.getId()) || targetUser.getRole().equals("ROLE_ADMIN") || targetUser.getRole().equals("ROLE_MANAGER")) {
                redirectAttributes.addFlashAttribute("message", "Manager không thể chỉnh sửa Admin hoặc Manager khác.");
                redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
                return false;
            }
        }
        return true;
    }

    @PostMapping("/sendEmailNotifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String sendEmailNotifications(
            @RequestParam String subject,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {

        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }

        List<String> emailsToSend = userService.getAllUsers().stream().map(User::getEmail).collect(Collectors.toList());

        for (String email : emailsToSend) {
            emailService.sendEmail(email, subject, content);
        }

        redirectAttributes.addFlashAttribute("message", "Email notifications sent successfully. Thông báo qua email đã được gửi thành công.");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/admin";
    }

    @PostMapping("/sendCustomEmailNotifications")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String sendCustomEmailNotifications(
            @RequestParam List<String> recipients,
            @RequestParam String subject,
            @RequestParam String content,
            RedirectAttributes redirectAttributes) {

        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }

        for (String email : recipients) {
            emailService.sendEmail(email, subject, content);
        }

        redirectAttributes.addFlashAttribute("message", "Email notifications sent successfully. Thông báo qua email đã được gửi thành công.");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/admin";
    }

    @PostMapping("/addNotification")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String addNotification(@RequestParam String title, @RequestParam String content, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }

        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setCreatedAt(LocalDateTime.now());

        notificationService.save(notification);

        redirectAttributes.addFlashAttribute("message", "Thông báo đã được thêm thành công");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        return "redirect:/admin";
    }

    @PostMapping("/deleteNotification")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String deleteNotification(@RequestParam Long notificationId, RedirectAttributes redirectAttributes) {
        if (!checkUserRole(userService, redirectAttributes)) {
            return "redirect:/login?roleChanged";
        }

        try {
            notificationService.deleteNotification(notificationId);
            redirectAttributes.addFlashAttribute("message", "Thông báo đã được xóa thành công");
            redirectAttributes.addFlashAttribute("alertClass", "alert-success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Có lỗi xảy ra khi xóa thông báo");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
        }
        return "redirect:/admin";
    }
}
