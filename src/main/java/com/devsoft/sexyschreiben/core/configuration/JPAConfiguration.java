package com.devsoft.sexyschreiben.core.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@ComponentScan(basePackages = {"com.devsoft.sexyschreiben.core.dao"})
@EnableJpaRepositories(basePackages = "com.devsoft.sexyschreiben.core.dao")
@EntityScan("com.devsoft.sexyschreiben.core.dao")
public class JPAConfiguration {
}
