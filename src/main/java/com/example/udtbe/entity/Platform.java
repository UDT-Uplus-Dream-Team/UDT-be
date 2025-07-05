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
@Table(name = "platform")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Platform extends TimeBaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "platform_id")
	private Long id;

	@Column(name = "platform_name", nullable = false)
	private String platformName;

	@Column(name = "is_deleted", nullable = false)
	private boolean isDeleted;

	@Builder(access = PRIVATE)
	private Platform(String platformName, boolean isDeleted) {
		this.platformName = platformName;
		this.isDeleted = isDeleted;
	}

	public static Platform of(String platformName, boolean isDeleted) {
		return Platform.builder()
			.platformName(platformName)
			.isDeleted(isDeleted)
			.build();
	}
}
