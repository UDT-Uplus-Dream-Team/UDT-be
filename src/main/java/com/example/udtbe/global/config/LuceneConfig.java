package com.example.udtbe.global.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LuceneConfig {

    @Bean
    public Analyzer koreanAnalyzer() {
        return new KoreanAnalyzer();
    }

    @Bean
    public Directory luceneDirectory() {
        return new ByteBuffersDirectory();
    }
}