package com.lyh.redislock.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("conductor.redis-lock")
public class RedisLockProperties {

    /** The redis server configuration to be used. */
    private REDIS_SERVER_TYPE serverType = REDIS_SERVER_TYPE.SINGLE;

    /** The address of the redis server following format -- host:port */
    private String serverAddress = "redis://127.0.0.1:6379";

    /** The password for redis authentication */
    private String serverPassword = null;

    /** The master server name used by Redis Sentinel servers and master change monitoring task */
    private String serverMasterName = "master";

    /** The namespace to use to prepend keys used for locking in redis */
    private String namespace = "";

    /**
     * Enable to otionally continue without a lock to not block executions until the locking service
     * becomes available
     */
    private boolean ignoreLockingExceptions = false;

    public REDIS_SERVER_TYPE getServerType() {
        return serverType;
    }

    public void setServerType(REDIS_SERVER_TYPE serverType) {
        this.serverType = serverType;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getServerMasterName() {
        return serverMasterName;
    }

    public void setServerMasterName(String serverMasterName) {
        this.serverMasterName = serverMasterName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean isIgnoreLockingExceptions() {
        return ignoreLockingExceptions;
    }

    public void setIgnoreLockingExceptions(boolean ignoreLockingExceptions) {
        this.ignoreLockingExceptions = ignoreLockingExceptions;
    }

    public enum REDIS_SERVER_TYPE {
        SINGLE,
        CLUSTER,
        SENTINEL
    }
}
