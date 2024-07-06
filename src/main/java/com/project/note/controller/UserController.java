package com.project.note.controller;

import com.project.note.dto.UserDto;
import com.project.note.model.Group;
import com.project.note.model.User;
import com.project.note.repository.UserRepository;
import com.project.note.service.Interface.GroupService;
import com.project.note.service.Interface.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupService groupService;

    // Show user info
    @GetMapping("/information")
    public String userInfo(Model model) {
        String username = getCurrentUsername();
        if (username != null) {
            User user = userService.findByUsername(username);
            UserDto userDto = UserDto.from(user);
            model.addAttribute("user", userDto);
            return "auth/UserInformation";
        }
        return "redirect:/auth/login";
    }

    @GetMapping("/details/{id}")
    public String showUserDetails(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            model.addAttribute("errorMessage", "Người dùng không tồn tại.");
            return "redirect:/group/choice";
        }

        String groupRole;
        Group group = groupService.findGroupByUserId(user.getId());
        if (group != null && group.getCreator().getId().equals(user.getId())) {
            groupRole = "@Leader";
        } else {
            groupRole = "@Member";
        }

        UserDto userDto = UserDto.from(user);
        model.addAttribute("user", userDto);
        model.addAttribute("groupRole", groupRole);
        return "user/details";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model) {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "auth/edit";
    }

    @PostMapping("/edit")
    public String updateUser(@RequestParam String fullName, @RequestParam String title, @RequestParam String email,
                             RedirectAttributes redirectAttributes, Model model) {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);

        if (!userService.isEmailUnique(email, user.getId())) {
            model.addAttribute("error", "Email đã tồn tại trong hệ thống.");
            model.addAttribute("fullName", fullName);
            model.addAttribute("title", title);
            model.addAttribute("email", email);
            return "auth/edit";
        }

        user.setFullName(fullName);
        user.setTitle(title);
        user.setEmail(email);
        userService.saveUser(user);

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công!");
        return "redirect:/user/information";
    }

    // Hiển thị form tải lên avatar
    @GetMapping("/avatar")
    public String avatarForm(Model model) {
        String username = getCurrentUsername();
        User user = userService.findByUsername(username);
        model.addAttribute("user", user);
        return "user/upload-avatar";
    }

    // Xử lý tải lên avatar
    @PostMapping("/{id}/avatar")
    public String handleAvatarUpload(@PathVariable("id") Long id,
                                     @RequestParam("avatar") MultipartFile file,
                                     RedirectAttributes redirectAttributes) {
        try {
            if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
                throw new RuntimeException("Định dạng file không hợp lệ. Chỉ hỗ trợ JPEG và PNG.");
            }
            if (file.getSize() > 10 * 1024 * 1024) { // 10MB
                throw new RuntimeException("Kích thước file vượt quá 10MB.");
            }
            userService.saveAvatar(id, file);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm ảnh đại diện thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi tải lên ảnh đại diện: " + e.getMessage());
        }
        return "redirect:/user/avatar";
    }

    // Xóa avatar
    @PostMapping("/{id}/avatar/delete")
    public String handleAvatarDelete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteAvatar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa ảnh đại diện thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa ảnh đại diện: " + e.getMessage());
        }
        return "redirect:/user/avatar";
    }

    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

}
