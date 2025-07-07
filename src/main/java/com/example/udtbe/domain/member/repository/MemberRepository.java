package com.example.udtbe.domain.member.repository;

import com.example.udtbe.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
