package com.book.igo.group.application;

import com.book.igo.group.application.dto.request.CreateGroupRequest;
import com.book.igo.group.application.dto.response.GetGroupResponse;
import com.book.igo.group.domain.repository.GroupImageRepository;
import com.book.igo.group.domain.repository.GroupRepository;
import com.book.igo.group.domain.repository.GroupTagRepository;
import com.book.igo.group.domain.repository.GroupUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupImageRepository groupImageRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupUserRepository groupUserRepository;

    @Transactional
    public GetGroupResponse create(Long userId, CreateGroupRequest request) {



        return null;
    }
}
