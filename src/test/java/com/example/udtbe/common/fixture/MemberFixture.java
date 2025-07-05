package com.example.udtbe.common.fixture;

import static com.example.udtbe.entity.enums.Gender.MAN;
import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.entity.Member;
import com.example.udtbe.entity.enums.Role;
import java.time.LocalDateTime;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class MemberFixture {

    public static Member member(String email, Role role) {
        return Member.of(
                email,
                "홍길동",
                null,
                role,
                MAN,
                LocalDateTime.of(2025, 7, 6, 1, 20),
                false
        );
    }

}
