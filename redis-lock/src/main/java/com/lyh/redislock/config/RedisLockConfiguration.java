package com.lyh.redislock.config;

import com.lyh.lock.api.Lock;
import com.lyh.redislock.lock.RedisLock;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableConfigurationProperties(RedisLockProperties.class)
@ConditionalOnProperty(name = "distributed.lock.type", havingValue = "redis")
public class RedisLockConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockConfiguration.class);

    @Bean
    public Redisson getRedisson(RedisLockProperties properties) {
        RedisServerType redisServerType;
        try {
            redisServerType = properties.getServerType();
        } catch (IllegalArgumentException ie) {
            final String message =
                    "Invalid Redis server type: "
                            + properties.getServerType()
                            + ", supported values are: "
                            + Arrays.toString(RedisServerType.values());
            LOGGER.error(message);
            throw new RuntimeException(message, ie);
        }
        String redisServerAddress = properties.getServerAddress();
        String redisServerPassword = properties.getServerPassword();
        String masterName = properties.getServerMasterName();

        Config redisConfig = new Config();

        int connectionTimeout = 10000;
        switch (redisServerType) {
            case SINGLE:
                redisConfig
                        .useSingleServer()
                        .setAddress(redisServerAddress)
                        .setPassword(redisServerPassword)
                        .setTimeout(connectionTimeout);
                break;
            case CLUSTER:
                redisConfig
                        .useClusterServers()
                        .setScanInterval(2000) // cluster state scan interval in milliseconds
                        .addNodeAddress(redisServerAddress.split(","))
                        .setPassword(redisServerPassword)
                        .setTimeout(connectionTimeout);
                break;
            case SENTINEL:
                redisConfig
                        .useSentinelServers()
                        .setScanInterval(2000)
                        .setMasterName(masterName)
                        .addSentinelAddress(redisServerAddress)
                        .setPassword(redisServerPassword)
                        .setTimeout(connectionTimeout);
                break;
        }

        return (Redisson) Redisson.create(redisConfig);
    }

    @Bean
    public Lock provideLock(Redisson redisson, RedisLockProperties properties) {
        return new RedisLock(redisson, properties);
    }
}
