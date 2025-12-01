package com.book.igo.group.application.dto.response;

import com.book.igo.group.domain.entity.Group;
import com.book.igo.group.domain.entity.GroupImage;
import com.book.igo.group.domain.entity.GroupTag;
import com.book.igo.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record GetGroupResponse(

        Long id,
        String title,
        String location,
        String locationDetail,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<String> images,
        List<String> tags,
        String description,
        int participantCount,
        int maxParticipants,
        CreatedBy createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int joinedCount
) {

    public static GetGroupResponse from(Group group) {

        // 이미지 정렬 + URL 리스트
        List<String> imageUrls = group.getImages().stream()
                .sorted(Comparator.comparingInt(GroupImage::getSortOrder))
                .map(GroupImage::getImageUrl)
                .toList();

        // 태그 이름 리스트
        List<String> tagNames = group.getGroupTags().stream()
                .map(GroupTag::getTag)
                .map(tag -> tag.getName())   // Tag 엔티티의 name 필드 기준
                .toList();

        // 현재 참여 인원: left_at == null 인 유저만 카운트
        long activeUserCount = group.getUsers().stream()
                .filter(gu -> gu.getLeftAt() == null)
                .count();

        User host = group.getHost();

        CreatedBy createdBy = new CreatedBy(
                host.getId(),
                host.getNickName(),
                host.getProfileImage()  // User 엔티티 필드명에 맞게 수정
        );

        return new GetGroupResponse(
                group.getId(),
                group.getTitle(),
                group.getLocation(),
                group.getLocationDetail(),
                group.getStartTime(),
                group.getEndTime(),
                imageUrls,
                tagNames,
                group.getDescription(),
                (int) activeUserCount,             // participantCount
                group.getMaxParticipants(),
                createdBy,
                group.getCreatedAt(),              // BaseTimeEntity 상속 전제
                group.getUpdatedAt(),
                (int) activeUserCount              // joinedCount (여기서는 동일 의미로 사용)
        );
    }

    public record CreatedBy(
            Long userId,
            String nickName,
            String profileImage
    ) {

    }
}
