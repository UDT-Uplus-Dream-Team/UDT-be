package com.example.udtbe.common.fixture;

import static com.example.udtbe.domain.member.entity.enums.Role.ROLE_ADMIN;
import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.admin.entity.Admin;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@NoArgsConstructor(access = PRIVATE)
public class AdminFixture {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static Admin admin(String email) {
        return Admin.of(
                email,
                passwordEncoder.encode("adminPassword"),
                "관리자",
                ROLE_ADMIN,
                LocalDateTime.now()
        );
    }
}
