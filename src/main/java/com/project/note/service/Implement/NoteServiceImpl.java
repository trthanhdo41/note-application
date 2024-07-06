package com.project.note.service.Implement;

import com.project.note.model.User;
import com.project.note.model.Note;
import com.project.note.repository.NoteRepository;
import com.project.note.service.Interface.NoteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Override
    public Note getNoteById(Long id) {
        return noteRepository.findById(id).orElse(null);
    }

    @Override
    public Note addNote(Note note, User user) {
        note.setTime(LocalDateTime.now());
        note.setUser(user);
        return noteRepository.saveAndFlush(note);
    }

    @Override
    public Note updateNote(Note note) {
        try {
            Optional<Note> existingNoteOptional = noteRepository.findById(note.getId());
            if (existingNoteOptional.isPresent()) {
                Note existingNote = existingNoteOptional.get();
                existingNote.setTitle(note.getTitle());
                existingNote.setContent(note.getContent());
                return noteRepository.saveAndFlush(existingNote);
            } else {
                throw new EntityNotFoundException("Note with id " + note.getId() + " not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update note with id " + note.getId(), e);
        }
    }

    @Override
    public void deleteNoteById(Long id) {
        noteRepository.deleteById(id);
    }

    @Override
    public void deleteAllNotesByUser(User user) {
        List<Note> userNotes = noteRepository.findByUser(user);
        noteRepository.deleteAll(userNotes);
    }

    @Override
    public List<Note> getNoteByContent(String content) {
        return noteRepository.findByContentStartingWith(content);
    }

    @Override
    public List<Note> getNotesByUserId(Long id) {
        return noteRepository.findByUser_Id(id);
    }

    @Override
    public Note findById(Long id) {
        return noteRepository.findById(id).orElse(null);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOldNotes() {
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(30);
        List<Note> notes = noteRepository.findAllTrashNotes(dateLimit);
        noteRepository.deleteAll(notes);
    }

    @Override
    public void restoreAllNotes() {
        List<Note> trashNotes = noteRepository.findAllByDeletedAtIsNotNull();
        for (Note note : trashNotes) {
            note.setDeletedAt(null);
            noteRepository.save(note);
        }
    }

    @Override
    public Note findNoteInTrashById(Long id) {
        return noteRepository.findByIdAndIsDeletedTrue(id).orElse(null);
    }

    @Override
    public void moveToTrash(Long id) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RuntimeException("Note not found"));
        note.setDeleted(true);
        note.setDeletedAt(LocalDateTime.now());
        noteRepository.save(note);
    }

    @Override
    public void save(Note note) {
        noteRepository.save(note);
    }

    @Override
    public void restoreAllNotesByUser(User user) {
        List<Note> deletedNotes = noteRepository.findAllInTrashByUser(user.getId());
        for (Note note : deletedNotes) {
            note.setDeleted(false);
            note.setDeletedAt(null);
        }
        noteRepository.saveAll(deletedNotes);
    }

    @Override
    public boolean isTrashEmpty() {
        return noteRepository.findAllInTrash().isEmpty();
    }

    @Override
    public List<Note> getAllActiveNotes() {
        return noteRepository.getAllActiveNotes();
    }

    @Override
    public void deleteAllNotesPermanently() {
        noteRepository.deleteAllInTrash();
    }
}
