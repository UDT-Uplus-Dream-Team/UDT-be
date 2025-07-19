package com.example.udtbe.domain.content.util;

import com.example.udtbe.domain.content.dto.response.PopularContentsResponse;
import java.util.List;

public interface PopularContentStore {

    List<PopularContentsResponse> get();

    void update();

    boolean isEmpty();
}
