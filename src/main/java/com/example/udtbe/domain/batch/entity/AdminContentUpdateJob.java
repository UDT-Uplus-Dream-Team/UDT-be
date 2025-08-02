package com.example.udtbe.domain.batch.entity;

import static lombok.AccessLevel.PRIVATE;

import com.example.udtbe.domain.admin.dto.common.AdminCategoryDTO;
import com.example.udtbe.domain.admin.dto.common.AdminPlatformDTO;
import com.example.udtbe.domain.batch.entity.enums.BatchStepStatus;
import com.example.udtbe.domain.batch.util.TimeUtil;
import com.example.udtbe.global.entity.TimeBaseEntity;
import com.example.udtbe.global.util.OptionalLongConverter;
import com.example.udtbe.global.util.OptionalTagConverter;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminContentUpdateJob extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_content_update_job_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private BatchStepStatus status;

    private LocalDateTime scheduledAt;

    private LocalDateTime finishedAt;

    private Long memberId;

    private Long contentId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String posterUrl;

    private String backdropUrl;

    private String trailerUrl;

    private LocalDateTime openDate;

    private int runningTime;

    private int episode;

    private String rating;

    @Type(JsonType.class)
    @Column(name = "categorys", columnDefinition = "longtext")
    private Map<String, AdminCategoryDTO> categories;

    @Type(JsonType.class)
    @Column(name = "platforms", columnDefinition = "longtext")
    private Map<String, AdminPlatformDTO> platforms;

    @Convert(converter = OptionalLongConverter.class)
    @Column(name = "directors")
    private List<Long> directors;

    @Convert(converter = OptionalLongConverter.class)
    @Column(name = "casts")
    private List<Long> casts;

    @Convert(converter = OptionalTagConverter.class)
    @Column(name = "countries")
    private List<String> countries;

    @Builder(access = PRIVATE)
    private AdminContentUpdateJob(BatchStepStatus status, LocalDateTime scheduledAt,
            Long memberId,
            Long contentId, String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl, LocalDateTime openDate, int runningTime, int episode, String rating,
            Map<String, AdminCategoryDTO> categories, Map<String, AdminPlatformDTO> platforms,
            List<Long> directors, List<Long> casts, List<String> countries) {

        this.status = status;
        this.scheduledAt = scheduledAt;
        this.memberId = memberId;
        this.contentId = contentId;
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.trailerUrl = trailerUrl;
        this.openDate = openDate;
        this.runningTime = runningTime;
        this.episode = episode;
        this.rating = rating;
        this.categories = categories;
        this.platforms = platforms;
        this.directors = directors;
        this.casts = casts;
        this.countries = countries;
    }

    public static AdminContentUpdateJob of(BatchStepStatus status, Long memberId,
            Long contentId,
            String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl, LocalDateTime openDate, int runningTime, int episode, String rating,
            Map<String, AdminCategoryDTO> categories, Map<String, AdminPlatformDTO> platforms,
            List<Long> directors, List<Long> casts, List<String> countries) {

        return AdminContentUpdateJob.builder()
                .status(status)
                .scheduledAt(getScheduledAt())
                .memberId(memberId)
                .contentId(contentId)
                .title(title)
                .description(description)
                .posterUrl(posterUrl)
                .backdropUrl(backdropUrl)
                .trailerUrl(trailerUrl)
                .openDate(openDate)
                .runningTime(runningTime)
                .episode(episode)
                .rating(rating)
                .categories(categories)
                .platforms(platforms)
                .casts(casts)
                .directors(directors)
                .countries(countries)
                .build();
    }

    public void changeStatus(BatchStepStatus status) {
        this.status = status;
    }

    private static LocalDateTime getScheduledAt() {
        return TimeUtil.getScheduledAt();
    }

    public void finish() {
        finishedAt = LocalDateTime.now();
    }

}
