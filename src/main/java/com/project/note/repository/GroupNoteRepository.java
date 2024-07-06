package com.project.note.repository;

import com.project.note.model.Group;
import com.project.note.model.GroupNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GroupNoteRepository extends JpaRepository<GroupNote, Long> {
    List<GroupNote> findByGroupId(Long groupId);

    @Query("SELECT n FROM GroupNote n WHERE n.group.id = :groupId AND (LOWER(n.noteContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(n.noteTitle) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<GroupNote> searchByContentAndGroupId(@Param("groupId") Long groupId, @Param("keyword") String keyword);

    List<GroupNote> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    @Transactional
    @Modifying
    @Query("DELETE FROM GroupNote gn WHERE gn.group.id = :groupId")
    void deleteAllNotesByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT gn FROM GroupNote gn WHERE gn.group = :group AND gn.noteContent LIKE CONCAT(:noteContent, '%')")
    List<GroupNote> findByGroupAndNoteContentStartingWith(@Param("group") Group group, @Param("noteContent") String noteContent);

}
