package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.dto.request.FeedbackContentGetRequest;
import com.example.udtbe.domain.content.entity.Feedback;
import com.example.udtbe.domain.member.entity.Member;
import java.util.List;

public interface FeedbackQueryDSL {

    List<Feedback> getFeedbacksByCursor(FeedbackContentGetRequest request, Member member);

}
