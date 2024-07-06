package com.project.note.dto;

import com.project.note.model.Group;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class GroupDto {
    private Long id;
    private String groupCode;
    private String groupName;
    private String createdAt;
    private String creatorName;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");

    public static GroupDto from(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setGroupCode(group.getGroupCode());
        dto.setGroupName(group.getGroupName());
        dto.setCreatedAt(group.getCreatedAt().format(FORMATTER));
        dto.setCreatorName(group.getCreator().getFullName());
        return dto;
    }
}
