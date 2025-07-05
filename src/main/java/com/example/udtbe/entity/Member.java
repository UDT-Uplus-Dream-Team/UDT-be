package com.example.udtbe.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.entity.enums.Gender;
import com.example.udtbe.entity.enums.Role;
import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Member extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder(access = PRIVATE)
    private Member(String email, String name, Role role, String profileImageUrl,
            Gender gender, LocalDateTime lastLoginAt, boolean isDeleted) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.gender = gender;
        this.lastLoginAt = lastLoginAt;
        this.isDeleted = isDeleted;
    }

    public static Member of(String email, String name, Role role, String profileImageUrl,
            Gender gender, LocalDateTime lastLoginAt, boolean isDeleted) {
        return Member.builder()
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .role(role)
                .gender(gender)
                .lastLoginAt(lastLoginAt)
                .isDeleted(isDeleted)
                .build();
    }
}
