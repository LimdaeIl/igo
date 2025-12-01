package com.book.igo.group.domain.repository;

import com.book.igo.group.domain.entity.Group;
import com.book.igo.group.domain.entity.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupUserRepository extends JpaRepository<GroupUser, Long> {

}
