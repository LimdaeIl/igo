package com.book.igo.group.application;

import com.book.igo.common.security.JwtUserPrincipal;
import com.book.igo.group.application.dto.request.CreateGroupRequest;
import com.book.igo.group.application.dto.response.GetGroupResponse;
import com.book.igo.group.domain.entity.Group;
import com.book.igo.group.domain.entity.GroupImage;
import com.book.igo.group.domain.entity.GroupRole;
import com.book.igo.group.domain.entity.GroupTag;
import com.book.igo.group.domain.entity.GroupUser;
import com.book.igo.group.domain.repository.GroupImageRepository;
import com.book.igo.group.domain.repository.GroupRepository;
import com.book.igo.group.domain.repository.GroupTagRepository;
import com.book.igo.group.domain.repository.GroupUserRepository;
import com.book.igo.group.infrastructure.exception.GroupErrorCode;
import com.book.igo.group.infrastructure.exception.GroupException;
import com.book.igo.tag.domain.entity.Tag;
import com.book.igo.tag.domain.repository.TagRepository;
import com.book.igo.user.domain.entity.User;
import com.book.igo.user.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupImageRepository groupImageRepository;
    private final GroupTagRepository groupTagRepository;
    private final GroupUserRepository groupUserRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    private final ImageStorageService imageStorageService;


    @Transactional
    public GetGroupResponse create(JwtUserPrincipal principal, CreateGroupRequest request,
            List<MultipartFile> imageFiles) {

        // 1) 호스트 유저 조회
        User host = userRepository.findById(principal.id())
                .orElseThrow(() -> new GroupException(
                        GroupErrorCode.HOST_USER_NOT_FOUND,
                        principal.id()
                ));

        // 2) 비즈니스 검증 (시간, 인원 등)
        validateCreateRequest(request);

        // 3) Group 엔티티 생성 및 저장
        Group group = Group.create(
                request.title(),
                request.location(),
                request.locationDetail(),
                request.startTime(),
                request.endTime(),
                request.description(),
                request.maxParticipants(),
                host
        );
        groupRepository.save(group);

        // 4) 이미지 저장
        saveGroupImages(group, imageFiles);

        // 5) 태그 저장 (요청 tags -> GroupTag)
        saveGroupTags(group, request.tags());

        // 6) 호스트를 모임 참가자로 등록 (HOST 역할)
        saveHostAsGroupUser(group, host);

        // 7) 응답 DTO 변환
        return GetGroupResponse.from(group);
    }

    private void validateCreateRequest(CreateGroupRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new GroupException(GroupErrorCode.INVALID_TIME_RANGE);
        }

        if (request.maxParticipants() == null || request.maxParticipants() < 2) {
            throw new GroupException(GroupErrorCode.INVALID_MAX_PARTICIPANTS);
        }
    }


    private void saveGroupImages(Group group, List<MultipartFile> imageFiles) {
        if (imageFiles == null || imageFiles.isEmpty()) {
            return;
        }

        List<GroupImage> entities = new ArrayList<>();

        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            if (file == null || file.isEmpty()) {
                continue;
            }

            // 1) S3(Lightsail) 업로드 → URL 획득
            String imageUrl = imageStorageService.uploadGroupImage(group.getId(), file, i);

            // 2) GroupImage 엔티티 생성
            GroupImage image = GroupImage.create(group, imageUrl, i);
            entities.add(image);
        }

        if (!entities.isEmpty()) {
            groupImageRepository.saveAll(entities);
        }
    }

    private void saveGroupTags(Group group, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        // 1) null, 공백 제거 + trim + 중복 제거
        List<String> normalized = tagNames.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(name -> !name.isBlank())
                .distinct()
                .toList();

        if (normalized.isEmpty()) {
            return;
        }

        // 2) 이미 존재하는 태그 조회
        List<Tag> existingTags = tagRepository.findByNameIn(normalized);

        Map<String, Tag> tagByName = existingTags.stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));

        // 3) 없는 태그는 새로 생성
        List<Tag> newTags = normalized.stream()
                .filter(name -> !tagByName.containsKey(name))
                .map(Tag::create)
                .toList();

        if (!newTags.isEmpty()) {
            List<Tag> saved = tagRepository.saveAll(newTags);
            // 새로 생성된 태그도 map에 추가
            saved.forEach(tag -> tagByName.put(tag.getName(), tag));
        }

        // 4) GroupTag 저장 (normalized 순서대로)
        List<GroupTag> groupTags = normalized.stream()
                .map(name -> {
                    Tag tag = tagByName.get(name); // 무조건 존재
                    return GroupTag.create(group, tag);
                })
                .toList();

        groupTagRepository.saveAll(groupTags);
    }

    private void saveHostAsGroupUser(Group group, User host) {
        GroupUser groupUser = GroupUser.create(group, host, GroupRole.HOST);
        groupUserRepository.save(groupUser);
    }
}

