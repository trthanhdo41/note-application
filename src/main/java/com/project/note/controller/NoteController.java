package com.project.note.controller;

import com.project.note.dto.NoteDto;
import com.project.note.dto.NoteTrashDto;
import com.project.note.dto.NotificationDto;
import com.project.note.dto.UserDto;
import com.project.note.model.Note;
import com.project.note.model.Notification;
import com.project.note.model.User;
import com.project.note.repository.NoteRepository;
import com.project.note.repository.UserRepository;
import com.project.note.service.ExcelService;
import com.project.note.service.Interface.NoteService;
import com.project.note.service.Interface.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;
    private final UserRepository userRepository;
    private final NoteRepository noteRepository;
    private final ExcelService excelService;
    private final NotificationService notificationService;

    @Autowired
    public NoteController(NoteService noteService, UserRepository userRepository, NoteRepository noteRepository, ExcelService excelService, NotificationService notificationService) {
        this.noteService = noteService;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.excelService = excelService;
        this.notificationService = notificationService;
    }

    private Optional<User> getAuthenticatedUser(Principal principal) {
        if (principal == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userRepository.findByUsername(principal.getName()));
    }

    // Show all notes
    @GetMapping("/list")
    public String getAllNote(Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/auth/login";
        }

        User user = optionalUser.get();
        UserDto userDto = UserDto.from(user);
        model.addAttribute("user", userDto);
        model.addAttribute("fullName", user.getFullName());
        List<Note> notes = noteRepository.findActiveNotesByUserIdOrderByTimeDesc(user.getId());

        List<NoteDto> noteDtos = notes.stream()
                .map(NoteDto::from)
                .collect(Collectors.toList());

        model.addAttribute("notes", noteDtos);
        return "note/index";
    }

    @GetMapping("/what-is-note-myself")
    public String whatIsNoteMySelf(Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optionalUser.get();
        UserDto userDto = UserDto.from(user);
        model.addAttribute("user", userDto);

        return "whatIsNoteMySelf/index";
    }

    // Find note by Content
    @GetMapping("/search/findByContentStartingWith/{content}")
    public String getNoteByContent(@PathVariable("content") String content, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        if (user != null) {
            List<Note> notes = noteRepository.findActiveNotesByContent(user.getId(), content);
            if (notes.isEmpty()) {
                model.addAttribute("message", "Không tìm thấy ghi chú nào có nội dung là " + content);
            } else {
                model.addAttribute("notes", notes);
            }
            model.addAttribute("fullName", user.getFullName());
        }
        return "note/index";
    }

    // Create note
    @GetMapping("/create")
    public String create(Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/auth/login";
        }

        User user = optionalUser.get();
        UserDto userDto = UserDto.from(user);
        model.addAttribute("user", userDto);
        model.addAttribute("note", new Note());
        return "note/addNote";
    }

    // Save note
    @PostMapping("/save")
    public String saveNote(@ModelAttribute("note") Note note, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        noteService.addNote(note, user);
        return "redirect:/note/list";
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Note note = noteService.getNoteById(id);

        if (note == null || !note.getUser().equals(user)) {
            return "redirect:/note/list";
        }

        UserDto userDto = UserDto.from(user);
        model.addAttribute("user", userDto);
        model.addAttribute("note", note);
        return "note/editNote";
    }

    // Update note
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("note") Note note, Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Note existingNote = noteService.getNoteById(id);

        if (existingNote == null || !existingNote.getUser().equals(user)) {
            return "redirect:/note/list";
        }

        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setTime(LocalDateTime.now());

        noteService.updateNote(existingNote);
        return "redirect:/note/list";
    }

    // Delete note by id (chuyển vào thùng rác)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Note note = noteService.getNoteById(id);

        if (note == null || !note.getUser().equals(user)) {
            return "redirect:/note/list";
        }

        note.setDeletedAt(LocalDateTime.now());
        noteRepository.save(note);
        return "redirect:/note/list";
    }

    // Delete all notes (chuyển tất cả vào thùng rác)
    @GetMapping("/delete/all")
    public String deleteAll(Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        List<Note> notes = noteService.getNotesByUserId(user.getId());
        for (Note note : notes) {
            note.setDeletedAt(LocalDateTime.now());
            noteRepository.save(note);
        }

        return "redirect:/note/list";
    }

    // Read note
    @GetMapping("/read/{id}")
    public String readNote(@PathVariable Long id, Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/auth/login";
        }

        User user = optionalUser.get();
        Note note = noteService.getNoteById(id);

        if (note == null || !note.getUser().equals(user)) {
            return "redirect:/note/list";
        }

        UserDto userDto = UserDto.from(user);
        model.addAttribute("user", userDto);
        NoteDto noteDto = NoteDto.from(note);
        model.addAttribute("note", noteDto);
        return "note/read";
    }


    // Các chức năng thùng rác
    // Show trash notes
    @GetMapping("/trash")
    public String listTrashNotes(Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(30);
        noteRepository.deleteOldTrashNotes(dateLimit);

        List<Note> notes = noteRepository.findAllTrashNotes(dateLimit);
        notes.removeIf(note -> !note.getUser().equals(user));

        List<NoteTrashDto> noteTrashDtos = notes.stream()
                .map(NoteTrashDto::from)
                .collect(Collectors.toList());

        model.addAttribute("notes", noteTrashDtos);

        UserDto userDto = UserDto.from(user);
        model.addAttribute("user", userDto);

        return "note/trash";
    }

    @GetMapping("/restore/{id}")
    public String restoreNote(@PathVariable Long id, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid note Id:" + id));

        if (!note.getUser().equals(user)) {
            return "redirect:/note/trash";
        }

        note.setDeletedAt(null);
        note.setDeleted(false);
        noteRepository.save(note);
        return "redirect:/note/trash";
    }

    @PostMapping("/permanent-delete")
    public String deleteAllNotesPermanently(RedirectAttributes redirectAttributes, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        List<Note> notes = noteRepository.findAllInTrashByUser(user.getId());
        if (notes.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Thùng rác trống, không có ghi chú nào để xóa.");
            return "redirect:/note/trash";
        } else {
            noteRepository.deleteAll(notes);
            redirectAttributes.addFlashAttribute("success", "Đã xóa vĩnh viễn tất cả ghi chú.");
            return "redirect:/note/trash";
        }
    }

    @GetMapping("/permanent-delete/{id}")
    public String permanentlyDeleteNote(@PathVariable Long id, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Note note = noteRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid note Id:" + id));

        if (!note.getUser().equals(user)) {
            return "redirect:/note/trash";
        }

        noteRepository.deleteById(id);
        return "redirect:/note/trash";
    }

    @GetMapping("/trash/search/{content}")
    public String searchTrashNotes(@PathVariable("content") String content, Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(30);
        List<Note> notes = noteRepository.searchTrashNotes(dateLimit, content);
        notes.removeIf(note -> !note.getUser().equals(user));
        model.addAttribute("notes", notes);
        return "note/trash";
    }

    @PostMapping("/restore-all")
    public String restoreAllNotes(Principal principal, RedirectAttributes redirectAttributes) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        boolean isTrashEmpty = noteService.isTrashEmpty();
        if (isTrashEmpty) {
            redirectAttributes.addFlashAttribute("error", "Thùng rác trống, không có ghi chú nào để khôi phục.");
            return "redirect:/note/trash";
        } else {
            noteService.restoreAllNotesByUser(user);
            redirectAttributes.addFlashAttribute("success", "Đã khôi phục tất cả ghi chú.");
            return "redirect:/note/trash";
        }
    }

    @GetMapping("/note/readInTrash/{id}")
    public String readInTrash(@PathVariable("id") Long id, Model model) {
        Note note = noteService.findNoteInTrashById(id);

        if (note != null) {
            model.addAttribute("note", note);
            return "note/readInTrash";
        } else {
            return "redirect:/note/trash";
        }
    }

    @PostMapping("/move-to-trash/{id}")
    public String moveToTrash(@PathVariable Long id) {
        noteService.moveToTrash(id);
        return "redirect:/note/list";
    }

    @PostMapping("/move-all-to-trash")
    public String moveAllNotesToTrash(Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        List<Note> notes = noteRepository.findByUserAndIsDeletedFalse(user);
        for (Note note : notes) {
            note.setDeleted(true);
            note.setDeletedAt(LocalDateTime.now());
            noteRepository.save(note);
        }

        return "redirect:/note/list";
    }

    // Read note in trash
    @GetMapping("/read-in-trash/{id}")
    public String readNoteInTrash(@PathVariable Long id, Model model, Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }

        User user = optionalUser.get();
        Note note = noteService.findNoteInTrashById(id);
        if (note == null || !note.getUser().equals(user)) {
            return "redirect:/note/trash";
        }

        NoteDto noteDto = NoteDto.from(note);
        model.addAttribute("note", noteDto);
        return "note/readInTrash";
    }


    // Download file Excel
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadNotes() {
        List<Note> notes = noteService.getAllActiveNotes();
        ByteArrayInputStream in = excelService.exportNotesToExcel(notes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=mynotes.xls");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    @GetMapping("/api/notifications")
    @ResponseBody
    public List<NotificationDto> getNotifications(Principal principal) {
        Optional<User> optionalUser = getAuthenticatedUser(principal);
        if (optionalUser.isEmpty()) {
            return Collections.emptyList();
        }

        User user = optionalUser.get();
        List<Notification> notifications = notificationService.getAllNotifications();
        return notifications.stream()
                .map(NotificationDto::from)
                .collect(Collectors.toList());
    }
}
