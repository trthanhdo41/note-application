package com.project.note.service.Interface;

import com.project.note.model.GroupNote;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface GroupNoteService {
    List<GroupNote> getAllGroupNotes();
    Optional<GroupNote> getGroupNoteById(Long id);
    GroupNote saveGroupNote(GroupNote groupNote);
    void deleteGroupNote(Long id);
    void deleteAllNotesByGroupId(Long groupId);
    void updateGroupNote(GroupNote existingGroupNote);
}
