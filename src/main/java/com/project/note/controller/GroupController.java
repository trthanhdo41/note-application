package com.project.note.controller;

import com.project.note.dto.GroupDto;
import com.project.note.dto.UserDto;
import com.project.note.model.Group;
import com.project.note.model.GroupMember;
import com.project.note.model.GroupNote;
import com.project.note.model.User;
import com.project.note.repository.GroupMemberRepository;
import com.project.note.repository.GroupNoteRepository;
import com.project.note.repository.GroupRepository;
import com.project.note.repository.UserRepository;
import com.project.note.service.ExcelService;
import com.project.note.service.Interface.GroupNoteService;
import com.project.note.service.Interface.GroupService;
import com.project.note.service.Interface.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    @Autowired
    private GroupNoteRepository groupNoteRepository;
    @Autowired
    private GroupNoteService groupNoteService;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private UserService userService;

    private static final Logger logger = Logger.getLogger(GroupController.class.getName());

    private String generateGroupCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000);
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/choice")
    public String showGroupChoice(Model model, HttpSession session, Principal principal, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "5") int size, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        if (user != null && user.getRole().contains("ROLE_USER")) {
            redirectAttributes.addFlashAttribute("vipMessage", "Bạn cần đăng ký tài khoản VIP Member để sử dụng tính năng này.");
            return "/note/registerVip";
        }

        String groupCode = generateGroupCode();
        session.setAttribute("groupCode", groupCode);
        model.addAttribute("groupCode", groupCode);

        if (user != null) {
            List<Group> myGroups = groupService.findGroupsByCreatorOrderByCreatedAtDesc(user);
            List<Group> joinedGroups = groupMemberRepository.findGroupsByUser(user);

            joinedGroups.removeIf(group -> group.getCreator().equals(user));

            int totalGroups = myGroups.size();
            int totalPages = (int) Math.ceil((double) totalGroups / size);

            int start = (page - 1) * size;
            int end = Math.min(start + size, totalGroups);
            List<Group> paginatedGroups = myGroups.subList(start, end);

            model.addAttribute("myGroups", paginatedGroups);
            model.addAttribute("joinedGroups", joinedGroups);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            model.addAttribute("size", size);
        }

        return "teamNote/choice";
    }


    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/create")
    public String showCreateGroupPage(Model model, HttpSession session) {
        String groupCode = (String) session.getAttribute("groupCode");
        model.addAttribute("groupCode", groupCode);
        return "teamNote/create";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/create")
    public String createGroup(@RequestParam String groupName, @RequestParam String groupCode, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return "redirect:/auth/login";
        }
        Group group = new Group();
        group.setGroupName(groupName);
        group.setGroupCode(groupCode);
        group.setCreator(user);
        groupService.saveGroup(group);

        redirectAttributes.addFlashAttribute("successMessage", "Tạo nhóm thành công!");
        return "redirect:/group/choice";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/delete/{id}")
    public String deleteGroup(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        groupService.deleteGroup(id);
        redirectAttributes.addFlashAttribute("successMessage", "Nhóm đã được xóa thành công!");
        return "redirect:/group/choice";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/details/{id}")
    public String showGroupDetails(@PathVariable Long id, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Group group = groupService.findById(id);
        if (group == null) {
            return "redirect:/group/choice";
        }
        User creator = group.getCreator();
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        String role;
        boolean isBlocked = false;

        if (currentUser != null) {
            GroupMember groupMember = groupMemberRepository.findByUserAndGroup(currentUser, group);
            if (groupMember != null) {
                isBlocked = groupMember.isBlocked();
            }
        }

        if (currentUser != null && currentUser.equals(creator)) {
            role = "Quản trị viên";
        } else {
            role = "Thành viên";
        }

        List<GroupMember> groupMembers = groupMemberRepository.findGroupMembersWithUsersByGroupId(id);
        int memberCount = groupMembers.size();
        int noteCount = group.getNotes().size();

        GroupDto groupDto = GroupDto.from(group);

        model.addAttribute("group", groupDto);
        model.addAttribute("creator", creator);
        model.addAttribute("groupMembers", groupMembers);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("noteCount", noteCount);
        model.addAttribute("role", role);
        model.addAttribute("isBlocked", isBlocked);

        if (isBlocked) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã bị chặn khỏi nhóm này.");
        }

        return "teamNote/groupDetails";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/updateName/{id}")
    public String updateGroupName(@PathVariable Long id, @RequestParam String groupName, RedirectAttributes redirectAttributes) {
        Group group = groupService.findById(id);
        if (group == null) {
            return "redirect:/group/choice";
        }
        group.setGroupName(groupName);
        groupService.saveGroup(group);
        redirectAttributes.addFlashAttribute("successMessage", "Tên nhóm đã được cập nhật thành công!");
        return "redirect:/group/details/" + id;
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/join")
    public String joinGroup(@RequestParam("groupCode") String groupCode, Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Group group = groupRepository.findByGroupCode(groupCode);
        if (group != null) {
            String username = principal.getName();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                GroupMember existingMember = groupMemberRepository.findByUserAndGroup(user, group);
                if (existingMember == null) {
                    GroupMember newMember = new GroupMember();
                    newMember.setUser(user);
                    newMember.setGroup(group);
                    groupMemberRepository.save(newMember);
                    return "redirect:/group/list?groupCode=" + groupCode;
                } else if (existingMember.isBlocked()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bạn đã bị chặn khỏi nhóm này.");
                    return "redirect:/group/choice";
                } else {
                    return "redirect:/group/list?groupCode=" + groupCode;
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Người dùng không tồn tại.");
                return "redirect:/group/choice";
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã nhóm không hợp lệ.");
            return "redirect:/group/choice";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/list")
    public String showGroupList(@RequestParam("groupCode") String groupCode, Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Group group = groupRepository.findByGroupCode(groupCode);
            if (group != null) {
                GroupMember groupMember = groupMemberRepository.findByUserAndGroup(user, group);
                if (groupMember != null && groupMember.isBlocked()) {
                    model.addAttribute("errorMessage", "Bạn đã bị chặn khỏi nhóm này.");
                    return "redirect:/group/choice";
                }
                return getAllGroupNote(group.getId(), model, principal);
            } else {
                model.addAttribute("errorMessage", "Nhóm không tồn tại.");
                return "redirect:/group/choice";
            }
        } else {
            model.addAttribute("errorMessage", "Người dùng không tồn tại.");
            return "redirect:/group/choice";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/leave/{id}")
    public String leaveGroup(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Group group = groupService.findById(id);
            if (group != null) {
                GroupMember groupMember = groupMemberRepository.findByUserAndGroup(user, group);
                if (groupMember != null) {
                    groupMemberRepository.delete(groupMember);
                    redirectAttributes.addFlashAttribute("successMessage", "Bạn đã rời khỏi nhóm thành công.");
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Bạn không phải là thành viên của nhóm này.");
                }
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Nhóm không tồn tại.");
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Người dùng không tồn tại.");
        }
        return "redirect:/group/choice";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/block-member/{id}")
    public String blockMember(@PathVariable Long id, @RequestParam Long groupId, RedirectAttributes redirectAttributes) {
        GroupMember member = groupMemberRepository.findById(id).orElse(null);
        if (member != null) {
            member.setBlocked(true);
            groupMemberRepository.save(member);
            redirectAttributes.addFlashAttribute("successMessage", "Đã chặn thành viên thành công!");
        }
        return "redirect:/group/details/" + groupId;
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/unblock-member/{id}")
    public String unblockMember(@PathVariable Long id, @RequestParam Long groupId, RedirectAttributes redirectAttributes) {
        GroupMember member = groupMemberRepository.findById(id).orElse(null);
        if (member != null) {
            member.setBlocked(false);
            groupMemberRepository.save(member);
            redirectAttributes.addFlashAttribute("successMessage", "Đã mở chặn thành viên thành công!");
        }
        return "redirect:/group/details/" + groupId;
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/addNote")
    public String addNoteForm(Model model, @RequestParam("groupId") Long groupId) {
        model.addAttribute("groupNote", new GroupNote());
        model.addAttribute("groupId", groupId);
        return "teamNote/addNote";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/saveNote")
    public String saveNote(@ModelAttribute GroupNote groupNote, @RequestParam("groupId") Long groupId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));
            groupNote.setGroup(group);
            groupNote.setUser(user);
            groupNote.setCreatedAt(LocalDateTime.now());
            groupNoteRepository.save(groupNote);
        } else {
            return "redirect:/login";
        }
        return "redirect:/group/listNotes?groupId=" + groupId;
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/listNotes")
    public String getAllGroupNote(@RequestParam("groupId") Long groupId, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String username = principal.getName();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

            GroupDto groupDto = GroupDto.from(group);
            UserDto userDto = UserDto.from(user);

            model.addAttribute("user", userDto);
            model.addAttribute("fullName", user.getFullName());
            model.addAttribute("group", groupDto);

            List<GroupNote> notes = groupNoteRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
            model.addAttribute("teamNotes", notes);

            return "teamNote/index";
        } else {
            return "redirect:/auth/login";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/search/findByGroupNoteContentStartingWith/{groupId}/{content}")
    public String getGroupNoteByContent(@PathVariable("groupId") Long groupId, @PathVariable("content") String content, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        User user = userRepository.findByUsername(username);

        if (user != null) {
            Group group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));

            List<GroupNote> notes = groupNoteRepository.findByGroupAndNoteContentStartingWith(group, content);
            if (notes.isEmpty()) {
                model.addAttribute("message", "Không tìm thấy ghi chú nào có nội dung là " + content);
            } else {
                model.addAttribute("teamNotes", notes);
            }
            model.addAttribute("group", GroupDto.from(group));
            model.addAttribute("fullName", user.getFullName());
        }
        return "teamNote/index";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("note/delete-all")
    public String deleteAllGroupNotes(@RequestParam("groupId") Long groupId, Model model) {
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            model.addAttribute("error", "Nhóm không tồn tại.");
            return "redirect:/group/list";
        }
        groupNoteService.deleteAllNotesByGroupId(groupId);
        return "redirect:/group/list?groupCode=" + group.getGroupCode();
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/note/delete/{noteId}")
    public String deleteGroupNote(@PathVariable Long noteId, @RequestParam("groupId") Long groupId, RedirectAttributes redirectAttributes) {
        GroupNote groupNote = groupNoteRepository.findById(noteId).orElse(null);
        if (groupNote == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ghi chú không tồn tại.");
            return "redirect:/group/listNotes?groupId=" + groupId;
        }
        groupNoteRepository.delete(groupNote);
        redirectAttributes.addFlashAttribute("successMessage", "Ghi chú đã được xóa thành công!");
        return "redirect:/group/listNotes?groupId=" + groupId;
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadGroupNotes(@RequestParam("groupId") Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));
        List<GroupNote> notes = groupNoteRepository.findByGroupIdOrderByCreatedAtDesc(groupId);

        ByteArrayInputStream in = excelService.exportGroupNotesToExcel(notes);

        HttpHeaders headers = new HttpHeaders();
        String filename = "group_notes_" + group.getGroupCode() + ".xls";
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(in));
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/note/read/{id}")
    public String showGroupNoteDetails(@PathVariable Long id, Model model, Principal principal) {
        GroupNote groupNote = groupNoteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group note ID"));

        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            UserDto userDto = UserDto.from(user);
            model.addAttribute("user", userDto);
        }

        Group group = groupNote.getGroup();
        GroupDto groupDto = GroupDto.from(group);
        model.addAttribute("group", groupDto);
        model.addAttribute("groupNote", groupNote);
        return "teamNote/groupNoteDetails";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @GetMapping("/editNote/{noteId}")
    public String showEditNoteForm(@PathVariable Long noteId, Model model, Principal principal) {
        GroupNote groupNote = groupNoteService.getGroupNoteById(noteId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid note Id:" + noteId));

        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            UserDto userDto = UserDto.from(user);
            model.addAttribute("user", userDto);
        }

        model.addAttribute("groupNote", groupNote);
        return "teamNote/editGroupNote";
    }

    @PreAuthorize("hasAnyRole('ROLE_VIPMEMBER', 'ROLE_ADMIN', 'ROLE_MANAGER')")
    @PostMapping("/updateNote")
    public String updateGroupNote(@ModelAttribute("groupNote") GroupNote groupNote, RedirectAttributes redirectAttributes) {
        GroupNote existingNote = groupNoteService.getGroupNoteById(groupNote.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid note Id:" + groupNote.getId()));
        existingNote.setNoteTitle(groupNote.getNoteTitle());
        existingNote.setNoteContent(groupNote.getNoteContent());
        groupNoteService.saveGroupNote(existingNote);
        redirectAttributes.addFlashAttribute("successMessage", "Ghi chú đã được cập nhật thành công!");
        return "redirect:/group/listNotes?groupId=" + existingNote.getGroup().getId();
    }

}

