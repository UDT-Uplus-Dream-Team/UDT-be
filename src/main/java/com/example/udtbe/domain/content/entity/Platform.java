package com.example.udtbe.domain.content.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.domain.content.entity.enums.PlatformType;
import com.example.udtbe.global.entity.TimeBaseEntity;
import com.example.udtbe.global.util.PlatformTypeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
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

    @Convert(converter = PlatformTypeConverter.class)
    @Column(name = "platform_type", nullable = false)
    private PlatformType platformType;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "platform", cascade = CascadeType.ALL)
    private List<ContentPlatform> contentPlatforms = new ArrayList<>();

    @Builder(access = PRIVATE)
    private Platform(PlatformType platformType, boolean isDeleted) {
        this.platformType = platformType;
        this.isDeleted = isDeleted;
    }

    public static Platform of(PlatformType platformType) {
        return Platform.builder()
                .platformType(platformType)
                .isDeleted(false)
                .build();
    }
}
