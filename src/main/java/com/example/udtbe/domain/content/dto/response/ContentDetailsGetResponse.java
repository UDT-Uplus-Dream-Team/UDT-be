package com.example.udtbe.domain.content.dto.response;


import com.example.udtbe.domain.content.dto.common.CastDTO;
import com.example.udtbe.domain.content.dto.common.PlatformDTO;
import com.example.udtbe.domain.content.entity.Cast;
import com.example.udtbe.domain.content.entity.Category;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.ContentPlatform;
import com.example.udtbe.domain.content.entity.Country;
import com.example.udtbe.domain.content.entity.Director;
import com.example.udtbe.domain.content.entity.Genre;
import java.time.LocalDateTime;
import java.util.List;

public record ContentDetailsGetResponse(

        Long contentId,
        String title,
        String description,
        String posterUrl,
        String backdropUrl,
        String trailerUrl,
        LocalDateTime openDate,
        int runningTime,
        int episode,
        String rating,
        List<PlatformDTO> platforms,
        List<CastDTO> casts,
        List<String> directors,
        List<String> countries,
        List<String> categories,
        List<String> genres

) {

    public ContentDetailsGetResponse(
            Content content,
            List<ContentPlatform> contentPlatforms,
            List<Cast> casts,
            List<Director> directors,
            List<Country> countries,
            List<Category> categories,
            List<Genre> genres
    ) {
        this(
                content.getId(),
                content.getTitle(),
                content.getDescription(),
                content.getPosterUrl(),
                content.getBackdropUrl(),
                content.getTrailerUrl(),
                content.getOpenDate(),
                content.getRunningTime(),
                content.getEpisode(),
                content.getRating(),
                contentPlatforms.stream()
                        .map(cp -> new PlatformDTO(
                                cp.getPlatform().getPlatformType().getType(), cp.getWatchUrl()
                        ))
                        .toList(),
                casts.stream()
                        .map(c -> new CastDTO(
                                c.getCastName(), c.getCastImageUrl()
                        ))
                        .toList(),
                directors.stream()
                        .map(d -> d.getDirectorName())
                        .toList(),
                countries.stream()
                        .map(c -> c.getCountryName())
                        .toList(),
                categories.stream()
                        .map(c -> c.getCategoryType().getType())
                        .toList(),
                genres.stream()
                        .map(g -> g.getGenreType().getType())
                        .toList()
        );
    }

}
