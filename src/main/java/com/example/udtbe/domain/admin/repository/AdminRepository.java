package com.example.udtbe.domain.admin.repository;

import com.example.udtbe.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Member, Long> {

}
