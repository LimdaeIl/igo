package com.book.igo.group.presentation;

import com.book.igo.common.response.ApiResponse;
import com.book.igo.group.application.GroupService;
import com.book.igo.group.application.dto.request.CreateGroupRequest;
import com.book.igo.group.application.dto.response.GetGroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
@RestController
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<ApiResponse<GetGroupResponse>> createGroup(
            @RequestParam Long userId,
            @RequestBody CreateGroupRequest request) {

        GetGroupResponse response = groupService.create(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));

    }
}
