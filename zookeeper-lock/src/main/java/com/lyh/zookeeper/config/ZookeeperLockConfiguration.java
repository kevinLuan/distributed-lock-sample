package com.lyh.zookeeper.config;

import com.lyh.lock.api.Lock;
import com.lyh.zookeeper.lock.ZookeeperLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ZookeeperProperties.class)
@ConditionalOnProperty(name = "distributed.lock.type", havingValue = "zookeeper")
public class ZookeeperLockConfiguration {

    @Bean
    public Lock provideLock(ZookeeperProperties properties) {
        return new ZookeeperLock(properties);
    }
}
