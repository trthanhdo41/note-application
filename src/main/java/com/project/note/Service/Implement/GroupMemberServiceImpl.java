package com.project.note.Service.Implement;

import com.project.note.Model.GroupMember;
import com.project.note.Repository.GroupMemberRepository;
import com.project.note.Service.Interface.GroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Override
    public List<GroupMember> getAllGroupMembers() {
        return groupMemberRepository.findAll();
    }

    @Override
    public Optional<GroupMember> getGroupMemberById(Long id) {
        return groupMemberRepository.findById(id);
    }

    @Override
    public GroupMember saveGroupMember(GroupMember groupMember) {
        return groupMemberRepository.save(groupMember);
    }

    @Override
    public void deleteGroupMember(Long id) {
        groupMemberRepository.deleteById(id);
    }
}
