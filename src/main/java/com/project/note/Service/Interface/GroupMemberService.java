package com.project.note.Service.Interface;

import com.project.note.Model.GroupMember;
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
