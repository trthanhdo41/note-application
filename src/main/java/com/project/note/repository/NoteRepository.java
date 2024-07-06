package com.project.note.repository;

import com.project.note.model.User;
import com.project.note.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByContentStartingWith(String content);

    List<Note> findByUser_Id(Long userId);

    List<Note> findByUser(User user);

    @Query("SELECT n FROM Note n WHERE n.deletedAt IS NULL")
    List<Note> findAllActiveNotes();

    // Tìm các ghi chú đã bị xóa nhưng chưa quá 30 ngày
    @Query("SELECT n FROM Note n WHERE n.deletedAt IS NOT NULL AND n.deletedAt > :dateLimit")
    List<Note> findAllTrashNotes(LocalDateTime dateLimit);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.deletedAt IS NULL")
    List<Note> findActiveNotesByUserId(Long userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.deletedAt IS NULL AND n.content LIKE %:content%")
    List<Note> findActiveNotesByContent(@Param("userId") Long userId, @Param("content") String content);

    @Query("SELECT n FROM Note n WHERE n.deletedAt IS NOT NULL AND n.deletedAt > :dateLimit AND n.content LIKE %:content%")
    List<Note> searchTrashNotes(@Param("dateLimit") LocalDateTime dateLimit, @Param("content") String content);

    List<Note> findAllByDeletedAtIsNotNull();

    Optional<Note> findByIdAndIsDeletedTrue(Long id);

    List<Note> findByUserAndIsDeletedFalse(User user);

    List<Note> findByUserAndIsDeletedTrue(User user);

    @Query("SELECT n FROM Note n WHERE n.isDeleted = true")
    List<Note> findAllInTrash();

    @Query("DELETE FROM Note n WHERE n.isDeleted = true")
    void deleteAllInTrash();

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.isDeleted = true")
    List<Note> findAllInTrashByUser(@Param("userId") Long userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.deletedAt IS NULL ORDER BY n.time DESC")
    List<Note> findActiveNotesByUserIdOrderByTimeDesc(@Param("userId") Long userId);

    @Query("SELECT n FROM Note n WHERE n.deletedAt IS NULL")
    List<Note> getAllActiveNotes();

    @Transactional
    @Modifying
    @Query("DELETE FROM Note n WHERE n.isDeleted = true AND n.deletedAt < :dateLimit")
    void deleteOldTrashNotes(LocalDateTime dateLimit);
}
