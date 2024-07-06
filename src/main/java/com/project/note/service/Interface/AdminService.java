package com.project.note.service.Interface;

import com.project.note.dto.AdminDto;

public interface AdminService {
    AdminDto getAdminByUsername(String username);
}
