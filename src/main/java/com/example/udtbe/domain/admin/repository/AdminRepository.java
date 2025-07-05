package com.example.udtbe.domain.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.udtbe.entity.Member;

public interface AdminRepository extends JpaRepository<Member, Long> {
}
