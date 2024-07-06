package com.project.note.repository;

import com.project.note.model.Group;
import com.project.note.model.GroupMember;
import com.project.note.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    @Query("SELECT gm FROM GroupMember gm WHERE gm.user = :user AND gm.group = :group")
    GroupMember findByUserAndGroup(@Param("user") User user, @Param("group") Group group);

    @Query("SELECT gm.user FROM GroupMember gm WHERE gm.group = :group")
    List<User> findByGroup(Group group);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user = :user")
    List<Group> findGroupsByUser(User user);

    @Query("SELECT gm FROM GroupMember gm JOIN FETCH gm.user WHERE gm.group.id = :groupId")
    List<GroupMember> findGroupMembersWithUsersByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.id = :userId")
    Group findGroupByUserId(@Param("userId") Long userId);
}
