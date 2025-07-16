package com.example.udtbe.domain.content.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content_platform")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentPlatform extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_platform_id")
    private Long id;

    @Column(name = "watch_url")
    private String watchUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "platform_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Platform platform;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "content_id",
            nullable = false,
            foreignKey = @ForeignKey(NO_CONSTRAINT))
    private Content content;

    private ContentPlatform(String watchUrl, Content content,
            Platform platform) {
        this.watchUrl = watchUrl;
        addContentAndPlatform(content, platform);
    }

    public static ContentPlatform of(String watchUrl, Content content,
            Platform platform) {
        return new ContentPlatform(watchUrl, content, platform);
    }

    private void addContentAndPlatform(Content content, Platform platform) {
        this.content = content;
        content.getContentPlatforms().add(this);
        this.platform = platform;
        platform.getContentPlatforms().add(this);
    }
}
