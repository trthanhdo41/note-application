package com.project.note.Service.Implement;

import com.project.note.Model.GroupNote;
import com.project.note.Repository.GroupNoteRepository;
import com.project.note.Service.Interface.GroupNoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupNoteServiceImpl implements GroupNoteService {

    @Autowired
    private GroupNoteRepository groupNoteRepository;

    @Override
    public List<GroupNote> getAllGroupNotes() {
        return groupNoteRepository.findAll();
    }

    @Override
    public Optional<GroupNote> getGroupNoteById(Long id) {
        return groupNoteRepository.findById(id);
    }

    @Override
    public GroupNote saveGroupNote(GroupNote groupNote) {
        return groupNoteRepository.save(groupNote);
    }

    @Override
    public void deleteGroupNote(Long id) {
        groupNoteRepository.deleteById(id);
    }

    @Override
    public void deleteAllNotesByGroupId(Long groupId) {
        groupNoteRepository.deleteAllNotesByGroupId(groupId);
    }

    @Override
    public void updateGroupNote(GroupNote existingGroupNote) {
        groupNoteRepository.save(existingGroupNote);
    }
}
