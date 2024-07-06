package com.project.note.repository;

import com.project.note.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<User> findMembersByGroupId(@Param("groupId") Long groupId);
    Optional<User> findById(Long id);
    List<User> findByUsernameContaining(String username);
    List<User> findByFullNameContaining(String fullName);
    List<User> findByEmailContaining(String email);

}
