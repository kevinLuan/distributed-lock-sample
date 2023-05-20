package com.lyh.zookeeper.config;

import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("conductor.zookeeper-lock")
public class ZookeeperProperties {

    /** The connection string to be used to connect to the Zookeeper cluster */
    private String connectionString = "localhost:2181";

    /** The session timeout for the curator */
    private Duration sessionTimeout =
            Duration.ofMillis(CuratorFrameworkFactory.builder().getSessionTimeoutMs());

    /** The connection timeout for the curator */
    private Duration connectionTimeout =
            Duration.ofMillis(CuratorFrameworkFactory.builder().getConnectionTimeoutMs());

    /** The namespace to use within the zookeeper cluster */
    private String namespace = "";

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public Duration getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Duration sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }
}
