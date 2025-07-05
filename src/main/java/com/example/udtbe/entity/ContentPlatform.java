package com.example.udtbe.entity;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PRIVATE;
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
import lombok.Builder;
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

    @Column(name = "is_available")
    private boolean isAvailable;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

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

    @Builder(access = PRIVATE)
    private ContentPlatform(String watchUrl, boolean isAvailable, boolean isDeleted,
            Platform platform, Content content) {
        this.watchUrl = watchUrl;
        this.isAvailable = isAvailable;
        this.isDeleted = isDeleted;
        this.platform = platform;
        this.content = content;
    }

    public static ContentPlatform of(String watchUrl, boolean isAvailable, boolean isDeleted,
            Platform platform, Content content) {
        return ContentPlatform.builder()
                .watchUrl(watchUrl)
                .isAvailable(isAvailable)
                .isDeleted(isDeleted)
                .platform(platform)
                .content(content)
                .build();
    }
}
