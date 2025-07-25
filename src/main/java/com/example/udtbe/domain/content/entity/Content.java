package com.example.udtbe.domain.content.entity;

import static lombok.AccessLevel.PRIVATE;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "poster_url", columnDefinition = "TEXT")
    private String posterUrl;

    @Column(name = "backdrop_url", columnDefinition = "TEXT")
    private String backdropUrl;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Column(name = "open_date")
    private LocalDateTime openDate;

    @Column(name = "running_time")
    private int runningTime;

    @Column(name = "episode")
    private int episode;

    @Column(name = "rating")
    private String rating;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<ContentPlatform> contentPlatforms = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<ContentCast> contentCasts = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<ContentDirector> contentDirectors = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<ContentCountry> contentCountries = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<ContentCategory> contentCategories = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL)
    private List<ContentGenre> contentGenres = new ArrayList<>();

    @Builder(access = PRIVATE)
    private Content(String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl, LocalDateTime openDate, int runningTime, int episode, String rating,
            boolean isDeleted) {
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.trailerUrl = trailerUrl;
        this.openDate = openDate;
        this.runningTime = runningTime;
        this.episode = episode;
        this.rating = rating;
        this.isDeleted = isDeleted;
    }

    public static Content of(String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl, LocalDateTime openDate, int runningTime, int episode,
            String rating) {
        return Content.builder()
                .title(title)
                .description(description)
                .posterUrl(posterUrl)
                .backdropUrl(backdropUrl)
                .trailerUrl(trailerUrl)
                .openDate(openDate)
                .runningTime(runningTime)
                .episode(episode)
                .rating(rating)
                .isDeleted(false)
                .build();
    }

    public void delete(boolean status) {
        this.isDeleted = status;
    }

    public void update(String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl, LocalDateTime openDate, int runningTime, int episode,
            String rating) {
        this.title = title;
        this.description = description;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.trailerUrl = trailerUrl;
        this.openDate = openDate;
        this.runningTime = runningTime;
        this.episode = episode;
        this.rating = rating;
    }

}
