package com.lyh.zookeeper.config;

import lombok.Data;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Data
@ConfigurationProperties("distributed.zookeeper-lock")
public class ZookeeperProperties {

    /**
     * The connection string to be used to connect to the Zookeeper cluster
     */
    private String connectionString = "localhost:2181";

    /**
     * The session timeout for the curator
     */
    private Duration sessionTimeout = Duration.ofMillis(CuratorFrameworkFactory.builder().getSessionTimeoutMs());

    /**
     * The connection timeout for the curator
     */
    private Duration connectionTimeout = Duration.ofMillis(CuratorFrameworkFactory.builder().getConnectionTimeoutMs());

    /**
     * The namespace to use within the zookeeper cluster
     */
    private String namespace = "/distributed/lock/";

    /**
     * The time (in milliseconds) for which the lock is leased for.
     */
    private Duration lockLeaseTime = Duration.ofMillis(60000);

    /**
     * The time (in milliseconds) for which the thread will block in an attempt to acquire the lock.
     */
    private Duration lockTimeToTry = Duration.ofMillis(500);
    /**
     * Used to enable/disable the execution lock.
     */
    private boolean executionLockEnabled = true;
}
