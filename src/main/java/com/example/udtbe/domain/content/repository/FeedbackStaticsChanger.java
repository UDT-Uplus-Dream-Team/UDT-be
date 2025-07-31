package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.enums.FeedbackType;
import com.example.udtbe.domain.content.entity.enums.GenreType;
import com.example.udtbe.domain.member.entity.Member;

public interface FeedbackStaticsChanger {

    long changeFeedbackStatics(Member member,
            GenreType genre,
            FeedbackType type,
            int countChange);

}
