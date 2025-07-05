package com.example.udtbe.entity;

import static jakarta.persistence.ConstraintMode.*;
import static jakarta.persistence.FetchType.*;
import static lombok.AccessLevel.*;

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
@Table(name = "content_cast")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ContentCast extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "content_cast_id")
	private Long id;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "cast_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Cast cast;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "content_id",
		nullable = false,
		foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Content content;

	@Builder(access = PRIVATE)
	private ContentCast(boolean isDeleted, Cast cast, Content content) {
		this.isDeleted = isDeleted;
		this.cast = cast;
		this.content = content;
	}

	public static ContentCast of(boolean isDeleted, Cast cast, Content content) {
		return ContentCast.builder()
			.isDeleted(isDeleted)
			.cast(cast)
			.content(content)
			.build();
	}
}
