package com.example.udtbe.domain.admin.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.member.entity.enums.Role;
import com.example.udtbe.global.entity.TimeBaseEntity;
import com.example.udtbe.global.util.RoleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admin")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Admin extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Convert(converter = RoleConverter.class)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder(access = PRIVATE)
    private Admin(String email, String password, String name, Role role,
            LocalDateTime lastLoginAt, boolean isDeleted) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.lastLoginAt = lastLoginAt;
        this.isDeleted = false;
    }

    public static Admin of(String email, String password, String name, Role role,
            LocalDateTime lastLoginAt) {
        return Admin.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .lastLoginAt(lastLoginAt)
                .build();
    }

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}