package com.lyh.lock.config;

import com.lyh.lock.api.Lock;
import com.lyh.lock.impl.LocalOnlyLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "distributed.lock.type", havingValue = "local_only")
public class LocalOnlyLockConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Lock provideLock() {
        return new LocalOnlyLock();
    }
}
