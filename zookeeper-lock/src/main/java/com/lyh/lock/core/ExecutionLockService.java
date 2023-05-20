package com.lyh.lock.core;

import com.lyh.lock.api.Lock;
import com.lyh.zookeeper.config.ZookeeperProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class ExecutionLockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionLockService.class);
    private final ZookeeperProperties properties;
    private final Lock lock;
    private final long lockLeaseTime;
    private final long lockTimeToTry;

    @Autowired
    public ExecutionLockService(ZookeeperProperties properties, Lock lock) {
        this.properties = properties;
        this.lock = lock;
        this.lockLeaseTime = properties.getLockLeaseTime().toMillis();
        this.lockTimeToTry = properties.getLockTimeToTry().toMillis();
    }

    /**
     * 尝试以合理的时间获取锁定以试用持续时间和租用时间。如果无法获取锁定，则退出。
     *
     * @param lockId
     * @return
     */
    public boolean acquireLock(String lockId) {
        return acquireLock(lockId, lockTimeToTry, lockLeaseTime);
    }

    public boolean acquireLock(String lockId, long timeToTryMs) {
        return acquireLock(lockId, timeToTryMs, lockLeaseTime);
    }

    public boolean acquireLock(String lockId, long timeToTryMs, long leaseTimeMs) {
        if (properties.isExecutionLockEnabled()) {
            if (!lock.acquireLock(lockId, timeToTryMs, leaseTimeMs, TimeUnit.MILLISECONDS)) {
                LOGGER.debug("Thread {} failed to acquire lock to lockId {}.", Thread.currentThread().getId(), lockId);
                return false;
            }
            LOGGER.debug("Thread {} acquired lock to lockId {}.", Thread.currentThread().getId(), lockId);
        }
        return true;
    }

    /**
     * Blocks until it gets the lock for workflowId
     *
     * @param lockId
     */
    public void waitForLock(String lockId) {
        if (properties.isExecutionLockEnabled()) {
            lock.acquireLock(lockId);
            LOGGER.debug("Thread {} acquired lock to lockId {}.", Thread.currentThread().getId(), lockId);
        }
    }

    public void releaseLock(String lockId) {
        if (properties.isExecutionLockEnabled()) {
            lock.releaseLock(lockId);
            LOGGER.debug("Thread {} released lock to lockId {}.", Thread.currentThread().getId(), lockId);
        }
    }

    public void deleteLock(String lockId) {
        if (properties.isExecutionLockEnabled()) {
            lock.deleteLock(lockId);
            LOGGER.debug("Thread {} deleted lockId {}.", Thread.currentThread().getId(), lockId);
        }
    }
}
