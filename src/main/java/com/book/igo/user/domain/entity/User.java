package com.book.igo.user.domain.entity;

import com.book.igo.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "v1_users",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_email", columnNames = {"email"})
        }
)
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false, length = 255)
    private Long id;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false,  length = 512)
    private String password;

    @Column(name = "nickName", nullable = false, length = 50)
    private String nickName;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "profile_image", length = 500)
    private String profileImage;

    @Column(name = "profile_message", length = 500)
    private String profileMessage;

    @Column(name = "mbti", length = 10)
    private String mbti; // enum 으로 대체 가능

    @Column(name = "notification_enabled", nullable = false)
    private boolean notificationEnabled = true;

    @Column(name = "isDeleted", nullable = false)
    private boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.USER;

    @Builder(access = AccessLevel.PUBLIC)
    private User(
            String email,
            String password,
            String nickName
    ) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.phoneNumber = null;
        this.profileImage = null;
        this.profileMessage = null;
        this.mbti = null;
        this.notificationEnabled = false;
        this.isDeleted = false;

    }


}
