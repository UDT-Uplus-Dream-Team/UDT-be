package com.example.udtbe.domain.member.repository;

import com.example.udtbe.domain.member.entity.Member;
import com.example.udtbe.domain.member.entity.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory qf;
    private final QMember member = QMember.member;

    @Override
    public List<Member> findMembersForAdmin(String cursor, int size, String keyword) {

        BooleanBuilder builder = new BooleanBuilder();

        if (keyword != null && !keyword.isEmpty() && keyword.trim().length() > 0) {
            builder.or(member.name.containsIgnoreCase(keyword))
                    .and(member.email.containsIgnoreCase(keyword));
        }

        if (StringUtils.hasText(cursor)) {
            long idCursor = Long.parseLong(cursor);
            builder.and(member.id.lt(idCursor));
        }

        return qf.selectFrom(member)
                .where(builder)
                .orderBy(member.id.desc())
                .limit(size)
                .fetch();
    }
}
