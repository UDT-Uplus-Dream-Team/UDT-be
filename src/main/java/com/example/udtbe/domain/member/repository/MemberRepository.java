package com.example.udtbe.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
