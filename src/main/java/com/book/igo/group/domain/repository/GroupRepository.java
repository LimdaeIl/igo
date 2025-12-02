package com.book.igo.group.domain.repository;


import com.book.igo.group.domain.entity.Group;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface GroupRepository extends JpaRepository<Group, Long> {

    @Query("""
            select distinct g
            from Group g
              left join g.groupTags gt
              left join gt.tag t
            where g.deletedAt is null
              and (:cursor is null or g.id < :cursor)
              and (
                   :keyword is null
                   or lower(g.title) like lower(concat('%', :keyword, '%'))
                   or lower(g.location) like lower(concat('%', :keyword, '%'))
                   or lower(t.name) like lower(concat('%', :keyword, '%'))
              )
            order by g.id desc
            """)
    List<Group> searchVisibleGroups(
            @Param("keyword") String keyword,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
