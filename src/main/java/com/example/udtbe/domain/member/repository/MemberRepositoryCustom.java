package com.example.udtbe.domain.member.repository;

import com.example.udtbe.domain.member.entity.Member;
import java.util.List;

public interface MemberRepositoryCustom {

    List<Member> findMembersForAdmin(String cursor, int size, String keyword);

}
