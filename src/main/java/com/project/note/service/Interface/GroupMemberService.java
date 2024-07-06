package com.project.note.service.Interface;

import com.project.note.model.GroupMember;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface GroupMemberService {
    List<GroupMember> getAllGroupMembers();
    Optional<GroupMember> getGroupMemberById(Long id);
    GroupMember saveGroupMember(GroupMember groupMember);
    void deleteGroupMember(Long id);
}
