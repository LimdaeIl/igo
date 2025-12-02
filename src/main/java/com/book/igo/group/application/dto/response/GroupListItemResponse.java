package com.book.igo.group.application.dto.response;

import com.book.igo.group.domain.entity.Group;
import com.book.igo.group.domain.entity.GroupImage;
import com.book.igo.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

public record GroupListItemResponse(
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
        GroupCreatedByResponse createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        int joinedCount
) {

    public static GroupListItemResponse from(Group group) {
        // 이미지: 정렬 순서 기준 + 최대 3개
        List<String> imageUrls = group.getImages().stream()
                .sorted(Comparator.comparingInt(GroupImage::getSortOrder))
                .map(GroupImage::getImageUrl)
                .limit(3)
                .toList();

        // 태그 이름 리스트
        List<String> tagNames = group.getGroupTags().stream()
                .map(gt -> gt.getTag().getName())
                .toList();

        // 현재 참여 중인 인원 (leftAt == null)
        int joinedCount = (int) group.getUsers().stream()
                .filter(gu -> gu.getLeftAt() == null)
                .count();

        int participantCount = joinedCount; // 지금은 joinedCount와 동일하게 사용

        return new GroupListItemResponse(
                group.getId(),
                group.getTitle(),
                group.getLocation(),
                group.getLocationDetail(),
                group.getStartTime(),
                group.getEndTime(),
                imageUrls,
                tagNames,
                group.getDescription(),
                participantCount,
                group.getMaxParticipants(),
                GroupCreatedByResponse.from(group.getHost()),
                group.getCreatedAt(),
                group.getUpdatedAt(),
                joinedCount
        );
    }

    public record GroupCreatedByResponse(
            Long userId,
            String nickName,
            String profileImage
    ) {
        public static GroupCreatedByResponse from(User user) {
            return new GroupCreatedByResponse(
                    user.getId(),
                    user.getNickName(),
                    user.getProfileImage()
            );
        }
    }
}
