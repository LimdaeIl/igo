package com.book.igo.group.domain.repository;

import com.book.igo.group.domain.entity.GroupImage;
import com.book.igo.group.domain.entity.GroupTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupTagRepository extends JpaRepository<GroupTag, Long> {

}
