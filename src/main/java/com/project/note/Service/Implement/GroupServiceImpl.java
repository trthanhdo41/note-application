package com.project.note.Service.Implement;

import com.project.note.DTO.GroupDto;
import com.project.note.Model.Group;
import com.project.note.Model.User;
import com.project.note.Repository.GroupMemberRepository;
import com.project.note.Repository.GroupRepository;
import com.project.note.Service.Interface.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Override
    public List<Group> findGroupsByCreatorOrderByCreatedAtDesc(User user) {
        return groupRepository.findByCreatorOrderByCreatedAtDesc(user);
    }

    @Override
    public Group findById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void saveGroup(Group group) {
        groupRepository.save(group);
    }

    @Override
    @Transactional
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }

    @Override
    public Group findByGroupCode(String groupCode) {
        return groupRepository.findByGroupCode(groupCode);
    }

    @Override
    public List<Group> findGroupsByUserOrderByCreatedAtDesc(User user) {
        List<Group> groups = groupMemberRepository.findGroupsByUser(user);
        groups.sort((g1, g2) -> g2.getCreatedAt().compareTo(g1.getCreatedAt()));
        return groups;
    }

    @Override
    public Group findGroupById(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    @Override
    public Group findGroupByUserId(Long id) {
        return groupMemberRepository.findGroupByUserId(id);
    }

    @Override
    public List<GroupDto> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groups.stream().map(GroupDto::from).collect(Collectors.toList());
    }

}
