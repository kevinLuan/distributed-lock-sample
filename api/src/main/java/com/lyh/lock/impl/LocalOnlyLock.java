package com.lyh.lock.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lyh.lock.api.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class LocalOnlyLock implements Lock {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalOnlyLock.class);

    private static final CacheLoader<String, Semaphore> LOADER =
            new CacheLoader<String, Semaphore>() {
                @Override
                public Semaphore load(String key) {
                    return new Semaphore(1, true);
                }
            };
    private static final ConcurrentHashMap<String, ScheduledFuture<?>> SCHEDULEDFUTURES =
            new ConcurrentHashMap<>();
    private static final LoadingCache<String, Semaphore> LOCKIDTOSEMAPHOREMAP =
            CacheBuilder.newBuilder().build(LOADER);
    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("LocalOnlyLock-scheduler");
    private static final ThreadFactory THREAD_FACTORY =
            runnable -> new Thread(THREAD_GROUP, runnable);
    private static final ScheduledExecutorService SCHEDULER =
            Executors.newScheduledThreadPool(1, THREAD_FACTORY);

    @Override
    public void acquireLock(String lockId) {
        LOGGER.trace("Locking {}", lockId);
        LOCKIDTOSEMAPHOREMAP.getUnchecked(lockId).acquireUninterruptibly();
    }

    @Override
    public boolean acquireLock(String lockId, long timeToTry, TimeUnit unit) {
        try {
            LOGGER.trace("Locking {} with timeout {} {}", lockId, timeToTry, unit);
            return LOCKIDTOSEMAPHOREMAP.getUnchecked(lockId).tryAcquire(timeToTry, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean acquireLock(String lockId, long timeToTry, long leaseTime, TimeUnit unit) {
        LOGGER.trace(
                "Locking {} with timeout {} {} for {} {}",
                lockId,
                timeToTry,
                unit,
                leaseTime,
                unit);
        if (acquireLock(lockId, timeToTry, unit)) {
            LOGGER.trace("Releasing {} automatically after {} {}", lockId, leaseTime, unit);
            SCHEDULEDFUTURES.put(
                    lockId, SCHEDULER.schedule(() -> releaseLock(lockId), leaseTime, unit));
            return true;
        }
        return false;
    }

    private void removeLeaseExpirationJob(String lockId) {
        ScheduledFuture<?> schedFuture = SCHEDULEDFUTURES.get(lockId);
        if (schedFuture != null && schedFuture.cancel(false)) {
            SCHEDULEDFUTURES.remove(lockId);
            LOGGER.trace("lockId {} removed from lease expiration job", lockId);
        }
    }

    @Override
    public void releaseLock(String lockId) {
        // Synchronized to prevent race condition between semaphore check and actual release
        // The check is here to prevent semaphore getting above 1
        // e.g. in case when lease runs out but release is also called
        synchronized (LOCKIDTOSEMAPHOREMAP) {
            if (LOCKIDTOSEMAPHOREMAP.getUnchecked(lockId).availablePermits() == 0) {
                LOGGER.trace("Releasing {}", lockId);
                LOCKIDTOSEMAPHOREMAP.getUnchecked(lockId).release();
                removeLeaseExpirationJob(lockId);
            }
        }
    }

    @Override
    public void deleteLock(String lockId) {
        LOGGER.trace("Deleting {}", lockId);
        LOCKIDTOSEMAPHOREMAP.invalidate(lockId);
    }

    @VisibleForTesting
    LoadingCache<String, Semaphore> cache() {
        return LOCKIDTOSEMAPHOREMAP;
    }

    @VisibleForTesting
    ConcurrentHashMap<String, ScheduledFuture<?>> scheduledFutures() {
        return SCHEDULEDFUTURES;
    }
}
