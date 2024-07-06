package com.project.note.repository;

import com.project.note.model.Group;
import com.project.note.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByCreatorOrderByCreatedAtDesc(User creator);

    @Query("SELECT g FROM Group g WHERE g.groupCode = :groupCode")
    Group findByGroupCode(@Param("groupCode") String groupCode);
}
