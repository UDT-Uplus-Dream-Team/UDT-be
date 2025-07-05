package com.example.udtbe.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.example.udtbe.global.entity.TimeBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "content")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Content extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "backdrop_url")
    private String backdropUrl;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Column(name = "open_date")
    private LocalDateTime openDate;

    @Column(name = "runtime_time")
    private int runtimeTime;

    @Column(name = "episode")
    private int episode;

    @Column(name = "rating")
    private String rating;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Builder(access = PRIVATE)
    private Content(String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl,
            LocalDateTime openDate, int runtimeTime, int episode, String rating,
            boolean isDeleted) {
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.trailerUrl = trailerUrl;
        this.openDate = openDate;
        this.runtimeTime = runtimeTime;
        this.episode = episode;
        this.rating = rating;
        this.isDeleted = isDeleted;
    }

    public static Content of(String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl,
            LocalDateTime openDate, int runtimeTime, int episode, String rating,
            boolean isDeleted) {
        return Content.builder()
                .title(title)
                .description(description)
                .posterUrl(posterUrl)
                .backdropUrl(backdropUrl)
                .trailerUrl(trailerUrl)
                .openDate(openDate)
                .runtimeTime(runtimeTime)
                .episode(episode)
                .rating(rating)
                .isDeleted(isDeleted)
                .build();
    }
}
