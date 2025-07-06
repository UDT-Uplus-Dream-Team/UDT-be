package com.example.udtbe.common.support;

import com.example.udtbe.common.config.TestJpaAuditingConfig;
import com.example.udtbe.global.config.QueryDslConfig;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.Repository;

@DataJpaTest(includeFilters = @ComponentScan.Filter(Repository.class))
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestJpaAuditingConfig.class, QueryDslConfig.class})
public abstract class DataJpaSupport extends TestContainerSupport {

    @Autowired
    protected EntityManager em;
}
