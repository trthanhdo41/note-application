package com.project.note.service.Implement;

import com.project.note.dto.GroupDto;
import com.project.note.model.Group;
import com.project.note.model.User;
import com.project.note.repository.GroupMemberRepository;
import com.project.note.repository.GroupRepository;
import com.project.note.service.Interface.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
