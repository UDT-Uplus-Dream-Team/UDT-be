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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private Content(String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl, LocalDateTime openDate, int runningTime, int episode, String rating,
            boolean isDeleted, List<ContentPlatform> contentPlatforms,
            List<ContentCast> contentCasts, List<ContentDirector> contentDirectors,
            List<ContentCountry> contentCountries, List<ContentCategory> contentCategories) {
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
        initContentPlatforms(contentPlatforms);
        initContentCasts(contentCasts);
        initContentDirectors(contentDirectors);
        initContentCountries(contentCountries);
        initContentCategories(contentCategories);
    }

    public static Content of(String title, String description, String posterUrl, String backdropUrl,
            String trailerUrl, LocalDateTime openDate, int runtimeTime, int episode, String rating,
            boolean isDeleted, List<ContentPlatform> contentPlatforms,
            List<ContentCast> contentCasts, List<ContentDirector> contentDirectors,
            List<ContentCountry> contentCountries, List<ContentCategory> contentCategories) {
        return new Content(
                title,
                description,
                posterUrl,
                backdropUrl,
                trailerUrl,
                openDate,
                runtimeTime,
                episode,
                rating,
                isDeleted,
                contentPlatforms,
                contentCasts,
                contentDirectors,
                contentCountries,
                contentCategories
        );
    }

    private void initContentPlatforms(List<ContentPlatform> contentPlatforms) {
        contentPlatforms.forEach(contentPlatform -> {
            this.contentPlatforms.add(contentPlatform);
            contentPlatform.addContent(this);
        });
    }

    private void initContentCasts(List<ContentCast> contentCasts) {
        contentCasts.forEach(contentCast -> {
            this.contentCasts.add(contentCast);
            contentCast.addContent(this);
        });
    }

    private void initContentDirectors(List<ContentDirector> contentDirectors) {
        contentDirectors.forEach(contentDirector -> {
            this.contentDirectors.add(contentDirector);
            contentDirector.addContent(this);
        });
    }

    private void initContentCountries(List<ContentCountry> contentCountries) {
        contentCountries.forEach(contentCountry -> {
            this.contentCountries.add(contentCountry);
            contentCountry.addContent(this);
        });
    }

    private void initContentCategories(List<ContentCategory> contentCategories) {
        contentCategories.forEach(contentCategory -> {
            this.contentCategories.add(contentCategory);
            contentCategory.addContent(this);
        });
    }
}
