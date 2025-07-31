package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.FeedbackStatics;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackStaticsRepository extends JpaRepository<FeedbackStatics, Long>,
        FeedbackStaticsChanger {

    Optional<FeedbackStatics> findByGenreType(GenreType genreType);

    Optional<FeedbackStatics> findByMemberAndGenreType(Member member, GenreType genre);

}
