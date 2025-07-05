package com.example.udtbe.domain.member.repository;

import com.example.udtbe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
