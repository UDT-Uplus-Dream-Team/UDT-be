package com.example.udtbe.entity;

import static lombok.AccessLevel.*;

import com.example.udtbe.global.entity.TimeBaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "director")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Director extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "director_id")
	private Long id;

	@Column(name = "director_name", nullable = false)
	private String directorName;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@Builder(access = PRIVATE)
	private Director(String directorName, boolean isDeleted) {
		this.directorName = directorName;
		this.isDeleted = isDeleted;
	}

	public static Director of(String directorName, boolean isDeleted) {
		return Director.builder()
			.directorName(directorName)
			.isDeleted(isDeleted)
			.build();
	}
}
