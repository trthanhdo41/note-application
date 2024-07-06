package com.project.note.Service.Implement;

import com.project.note.DTO.AdminDto;
import com.project.note.DTO.UserDto;
import com.project.note.Model.User;
import com.project.note.Repository.AdminRepository;
import com.project.note.Repository.UserRepository;
import com.project.note.Service.Interface.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminDto getAdminByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return AdminDto.from(user);
    }


}
