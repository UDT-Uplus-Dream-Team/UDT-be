package com.example.udtbe.domain.content.util;

import com.example.udtbe.domain.content.dto.response.PopularContentByPlatformResponse;
import java.util.List;

public interface PopularContentByPlatformStore {

    List<PopularContentByPlatformResponse> get();

    void update();

    boolean isEmpty();
}