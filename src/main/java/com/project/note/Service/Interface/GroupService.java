package com.project.note.Service.Interface;

import com.project.note.DTO.GroupDto;
import com.project.note.Model.Group;
import com.project.note.Model.User;

import java.util.Collection;
import java.util.List;

public interface GroupService {
    List<Group> findGroupsByCreatorOrderByCreatedAtDesc(User user);
    Group findById(Long id);
    void saveGroup(Group group);
    void deleteGroup(Long id);
    Group findByGroupCode(String groupCode);
    List<Group> findGroupsByUserOrderByCreatedAtDesc(User user);
    Group findGroupById(Long groupId);
    Group findGroupByUserId(Long id);
    List<GroupDto> getAllGroups();
}
