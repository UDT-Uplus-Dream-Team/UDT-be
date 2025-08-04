package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.FeedbackStatistics;
import java.util.List;

public interface FeedbackStatisticsRepositoryCustom {
    
    List<FeedbackStatistics> findByMemberIds(List<Long> memberIds);
}
