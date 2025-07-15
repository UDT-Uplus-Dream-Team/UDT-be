package com.example.udtbe.domain.content.dto;

import com.example.udtbe.domain.content.dto.common.CuratedContentDTO;
import com.example.udtbe.domain.content.entity.Content;
import com.example.udtbe.domain.content.entity.CuratedContent;
import java.util.List;

public class CuratedContentMapper {

    public static CuratedContentDTO toResponse(CuratedContent curatedContent) {
        Content content = curatedContent.getContent();

        return new CuratedContentDTO(
                content.getId(),
                content.getTitle(),
                content.getPosterUrl(),
                content.getRating(),
                ContentCountryMapper.countryNames(content.getContentCountries()),
                ContentCategoryMapper.toCategoryDTOList(
                        content.getContentCategories(),
                        content.getContentGenres()
                ),
                ContentPlatformMapper.platformNames(content.getContentPlatforms())
        );
    }

    public static List<CuratedContentDTO> toResponseList(List<CuratedContent> curatedContents) {
        return curatedContents.stream()
                .map(CuratedContentMapper::toResponse)
                .toList();
    }
}
