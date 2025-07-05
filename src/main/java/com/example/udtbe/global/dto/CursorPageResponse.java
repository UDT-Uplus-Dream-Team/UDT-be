package com.example.udtbe.global.dto;

import java.util.List;

public record CursorPageResponse<T>(
	List<T> item,
	String nextCursor,
	boolean hasNext
) {
}
