package com.project.note.service.Interface;

import com.project.note.dto.GroupDto;
import com.project.note.model.Group;
import com.project.note.model.User;

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
