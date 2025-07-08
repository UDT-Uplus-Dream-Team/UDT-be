package com.example.udtbe.domain.content.entity;

import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "platform")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Platform extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "platform_id")
    private Long id;

    @Column(name = "platform_name", nullable = false)
    private String platformName;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL)
    private List<ContentPlatform> contentPlatforms = new ArrayList<>();

    private Platform(String platformName, boolean isDeleted,
            List<ContentPlatform> contentPlatforms) {
        this.platformName = platformName;
        this.isDeleted = isDeleted;
        initContentPlatforms(contentPlatforms);
    }

    public static Platform of(String platformName, boolean isDeleted,
            List<ContentPlatform> contentPlatforms) {
        return new Platform(platformName, isDeleted, contentPlatforms);
    }

    private void initContentPlatforms(List<ContentPlatform> contentPlatforms) {
        contentPlatforms.forEach(contentPlatform -> {
            this.contentPlatforms.add(contentPlatform);
            contentPlatform.addPlatform(this);
        });
    }
}
