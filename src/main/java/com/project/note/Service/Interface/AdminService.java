package com.project.note.Service.Interface;

import com.project.note.DTO.AdminDto;
import com.project.note.DTO.UserDto;

import java.util.List;

public interface AdminService {
    AdminDto getAdminByUsername(String username);
}
