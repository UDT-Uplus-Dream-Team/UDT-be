package com.example.udtbe.member.repository;

import com.example.udtbe.common.support.DataJpaSupport;
import com.example.udtbe.domain.member.service.MemberQuery;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("[MemberRepository 테스트]")
class MemberRepositoryTest extends DataJpaSupport {

    @Autowired
    MemberQuery memberQuery;
}
