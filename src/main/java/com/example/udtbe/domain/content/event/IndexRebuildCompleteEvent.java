package com.example.udtbe.domain.content.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class IndexRebuildCompleteEvent extends ApplicationEvent {

    private final int indexedCount;
    private final long buildTimeMs;

    public IndexRebuildCompleteEvent(Object source, int indexedCount, long buildTimeMs) {
        super(source);
        this.indexedCount = indexedCount;
        this.buildTimeMs = buildTimeMs;
    }

    public static IndexRebuildCompleteEvent of(Object source, int indexedCount, long buildTimeMs) {
        return new IndexRebuildCompleteEvent(source, indexedCount, buildTimeMs);
    }
}