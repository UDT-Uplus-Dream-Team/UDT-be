package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackStatisticsRepository extends JpaRepository<FeedbackStatistics, Long>,
        FeedbackStatisticsChanger, FeedbackStatisticsRepositoryCustom {

    List<FeedbackStatistics> findByMemberIdAndIsDeletedFalse(Long memberId);

    Optional<FeedbackStatistics> findByGenreType(GenreType genreType);

    List<FeedbackStatistics> findByMemberIds(List<Long> memberIds);

}
