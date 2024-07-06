package com.project.note.service.Implement;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.project.note.dto.UserDto;
import com.project.note.model.AccountActivationToken;
import com.project.note.model.PasswordResetToken;
import com.project.note.model.User;
import com.project.note.repository.AccountActivationTokenRepository;
import com.project.note.repository.PasswordResetTokenRepository;
import com.project.note.repository.UserRepository;
import com.project.note.service.EmailService;
import com.project.note.service.Interface.UserService;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final DefaultCodeVerifier verifier;
    private final Path rootLocation = Paths.get("uploads/avatar");
    private final AccountActivationTokenRepository accountActivationTokenRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, PasswordResetTokenRepository passwordResetTokenRepository, AccountActivationTokenRepository accountActivationTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.accountActivationTokenRepository = accountActivationTokenRepository;
        CodeGenerator codeGenerator = new DefaultCodeGenerator(HashingAlgorithm.SHA1);
        this.verifier = new DefaultCodeVerifier(codeGenerator, new SystemTimeProvider());
        this.verifier.setAllowedTimePeriodDiscrepancy(1);
    }

    @Override
    @Transactional
    public void registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("Tài khoản đã tồn tại");
        }
        user.setRole("ROLE_USER");
        user.setActive(0);
        user.setRegistrationTime(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.saveAndFlush(user);

        String token = UUID.randomUUID().toString();
        AccountActivationToken activationToken = new AccountActivationToken();
        activationToken.setToken(token);
        activationToken.setUser(user);
        activationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        accountActivationTokenRepository.save(activationToken);

        String activationLink = "http://localhost:8080/auth/activate?token=" + token;
        String subject = "Kích hoạt tài khoản của bạn";
        String text = "Chúc mừng " + user.getFullName() + " đã đăng ký tài khoản " + user.getUsername() + " thành công, bấm vào link này để kích hoạt tài khoản của bạn: " + activationLink;

        emailService.sendEmail(user.getEmail(), subject, text);
    }

    @Override
    public boolean existsByUsername(String user) {
        return userRepository.findByUsername(user) != null;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User findByUsername(String name) {
        return userRepository.findByUsername(name);
    }

    @Override
    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username);
        }
        return null;
    }

    @Override
    public void updateUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean isEmailUnique(String email, Long userId) {
        User user = userRepository.findByEmail(email);
        return user == null || user.getId().equals(userId);
    }

    @Override
    @Transactional
    public String forgotPasswordUseEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email chưa được đăng ký.");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);

        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByUser(user);
        if (passwordResetToken == null) {
            passwordResetToken = new PasswordResetToken();
            passwordResetToken.setUser(user);
        }
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(expiryDate);
        passwordResetTokenRepository.save(passwordResetToken);

        String resetUrl = "http://localhost:8080/auth/reset-password-form?token=" + token;
        String subject = "Password Reset Request";
        String content = "Bấm vào đây để khôi phục mật khẩu của tài khoản " + user.getUsername() + " nhé " + user.getFullName() + ": " + resetUrl;

        emailService.sendEmail(user.getEmail(), subject, content);

        return "Password reset email sent.";
    }

    @Override
    @Transactional
    public String resetPasswordUseEmail(String token, String newPassword) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Không hợp lệ, bạn đã vừa thực hiện thay đổi mật khẩu xong."));

        if (passwordResetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token đã hết hạn.");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(passwordResetToken);

        return "Password reset successful.";
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Map<String, String> generateTwoFactorAuthentication(User user, HttpServletRequest request) {
        SecretGenerator secretGenerator = new DefaultSecretGenerator();
        String tempSecret = secretGenerator.generate();

        String totpUrl = "otpauth://totp/NoteApp:" + user.getUsername() + "?secret=" + tempSecret + "&issuer=NoteApp";

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(totpUrl, BarcodeFormat.QR_CODE, 200, 200);
        } catch (WriterException e) {
            throw new RuntimeException("Error generating QR code");
        }

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        try {
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        } catch (IOException e) {
            throw new RuntimeException("Error writing QR code to stream");
        }

        String qrImage = Base64.getEncoder().encodeToString(pngOutputStream.toByteArray());
        String qrDataUri = "data:image/png;base64," + qrImage;

        Map<String, String> response = new HashMap<>();
        response.put("qrDataUri", qrDataUri);
        response.put("totpSecret", tempSecret);

        request.getSession().setAttribute("tempTotpSecret", tempSecret);

        return response;
    }

    @Override
    @Transactional
    public String enableTwoFactorAuthentication(User user, String totpCode, HttpServletRequest httpServletRequest) {
        String tempTotpSecret = (String) httpServletRequest.getSession().getAttribute("tempTotpSecret");
        if (tempTotpSecret == null) {
            return "Không tìm thấy mã TOTP tạm thời";
        }

        if (!verifier.isValidCode(tempTotpSecret, totpCode)) {
            return "Mã xác thực không đúng";
        }

        user.setTotpSecret(tempTotpSecret);
        userRepository.save(user);

        httpServletRequest.getSession().removeAttribute("tempTotpSecret");

        return "success";
    }

    @Override
    public String disableTwoFactorAuthentication(User user) {
        if (user.getTotpSecret() == null) {
            return "2FA chưa được bật cho tài khoản này";
        }
        user.setTotpSecret(null);
        userRepository.save(user);
        return "success";
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void saveAvatar(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            String fileType = file.getContentType();
            if (fileType == null || !(fileType.equals("image/jpeg") || fileType.equals("image/png"))) {
                throw new IllegalArgumentException("Invalid file type - Định dạng file không hợp lệ. Chỉ hỗ trợ JPEG và PNG.");
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = this.rootLocation.resolve(fileName);

            Files.createDirectories(this.rootLocation);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            user.setAvatar("/uploads/avatar/" + fileName);
            userRepository.save(user);

        } catch (IOException e) {
            throw new RuntimeException("Failed to store avatar", e);
        }
    }

    @Override
    public void deleteAvatar(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getAvatar() != null) {
            try {
                Path filePath = Paths.get("uploads/avatar").resolve(user.getAvatar().replace("/uploads/avatar/", ""));
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete avatar", e);
            }

            user.setAvatar(null);
            userRepository.save(user);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<UserDto> searchUsers(String searchType, String keyword) {
        List<User> users;
        switch (searchType) {
            case "id":
                Long id = Long.parseLong(keyword);
                users = userRepository.findById(id).map(Collections::singletonList).orElse(Collections.emptyList());
                break;
            case "username":
                users = userRepository.findByUsernameContaining(keyword);
                break;
            case "fullName":
                users = userRepository.findByFullNameContaining(keyword);
                break;
            case "email":
                users = userRepository.findByEmailContaining(keyword);
                break;
            default:
                users = new ArrayList<>();
        }
        return users.stream().map(UserDto::from).collect(Collectors.toList());
    }

    @Override
    public List<UserDto> searchUsersById(Long id) {
        return userRepository.findById(id)
                .map(user -> Collections.singletonList(UserDto.from(user)))
                .orElse(Collections.emptyList());
    }

    @Override
    public List<UserDto> searchUsersByUsername(String username) {
        return userRepository.findByUsernameContaining(username).stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> searchUsersByFullName(String fullName) {
        return userRepository.findByFullNameContaining(fullName).stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> searchUsersByEmail(String email) {
        return userRepository.findByEmailContaining(email).stream()
                .map(UserDto::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public boolean verifyTwoFactorAuthenticationCode(User user, String totpCode) {
        return verifier.isValidCode(user.getTotpSecret(), totpCode);
    }

    @Override
    @Transactional
    public void updateUserDetails(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setTitle(updatedUser.getTitle());
        existingUser.setRole("ROLE_" + updatedUser.getRole());
        existingUser.setActive(updatedUser.getActive());
        existingUser.setTotpSecret(updatedUser.getTotpSecret());

        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void updateUserRole(String username, String role) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            user.setRole(role);
            userRepository.saveAndFlush(user);
        }
    }


}
