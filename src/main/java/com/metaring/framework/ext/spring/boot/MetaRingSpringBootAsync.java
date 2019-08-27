package com.metaring.framework.ext.spring.boot;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
class MetaRingSpringBootAsync {

    @Bean("MetaRingAsyncExecutor")
    static final Executor MetaRingAsyncExecutor() {
        return MetaRingSpringBootApplication.EXECUTOR;
    }
}
