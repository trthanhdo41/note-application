package com.project.note.service.Implement;

import com.project.note.dto.AdminDto;
import com.project.note.model.User;
import com.project.note.repository.AdminRepository;
import com.project.note.repository.UserRepository;
import com.project.note.service.Interface.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
