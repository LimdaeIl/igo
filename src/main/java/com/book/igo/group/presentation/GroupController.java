package com.book.igo.group.presentation;

import com.book.igo.common.response.ApiResponse;
import com.book.igo.common.security.JwtUserPrincipal;
import com.book.igo.group.application.GroupService;
import com.book.igo.group.application.dto.request.CreateGroupRequest;
import com.book.igo.group.application.dto.response.GetGroupResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@RequestMapping("/api/v1/groups")
@RestController
public class GroupController {

    private final GroupService groupService;


    @PreAuthorize("hasRole('USER')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<GetGroupResponse>> createGroup(
            @AuthenticationPrincipal JwtUserPrincipal principal,
            @RequestPart("request") @Valid CreateGroupRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {

        GetGroupResponse response = groupService.create(principal, request, images);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
