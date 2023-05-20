package com.lyh.redislock.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ToString
@ConfigurationProperties("distributed.redis-lock")
public class RedisLockProperties {

    /**
     * The redis server configuration to be used.
     */
    private RedisServerType serverType = RedisServerType.SINGLE;

    /**
     * The address of the redis server following format -- host:port
     */
    private String serverAddress = "redis://127.0.0.1:6379";

    /**
     * The password for redis authentication
     */
    private String serverPassword = null;

    /**
     * The master server name used by Redis Sentinel servers and master change monitoring task
     */
    private String serverMasterName = "master";

    /**
     * The namespace to use to prepend keys used for locking in redis
     */
    private String namespace = "";

    /**
     * Enable to otionally continue without a lock to not block executions until the locking service
     * becomes available
     */
    private boolean ignoreLockingExceptions = false;
}
