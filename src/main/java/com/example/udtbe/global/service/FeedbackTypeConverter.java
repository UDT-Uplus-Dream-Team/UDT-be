package com.example.udtbe.global.service;

import com.example.udtbe.entity.enums.FeedbackType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class FeedbackTypeConverter extends CommonConverter implements AttributeConverter<FeedbackType, String> {

	@Override
	public FeedbackType convertToEntityAttribute(String feedbackType) {
		validateNotNull(feedbackType);
		return FeedbackType.from(feedbackType);
	}

	@Override
	public String convertToDatabaseColumn(FeedbackType feedbackType) {
		validateNotNull(feedbackType);
		return feedbackType.getType();
	}
}
