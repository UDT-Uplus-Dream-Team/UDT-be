package com.example.udtbe.domain.content.repository;

import com.example.udtbe.domain.content.entity.Content;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentRepositoryCustom {

    List<Content> findAllById(Iterable<Long> contentIds);
}
