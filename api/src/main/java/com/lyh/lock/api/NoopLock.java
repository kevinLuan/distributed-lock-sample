package com.lyh.lock.api;

import java.util.concurrent.TimeUnit;

public class NoopLock implements Lock {

    @Override
    public void acquireLock(String lockId) {}

    @Override
    public boolean acquireLock(String lockId, long timeToTry, TimeUnit unit) {
        return true;
    }

    @Override
    public boolean acquireLock(String lockId, long timeToTry, long leaseTime, TimeUnit unit) {
        return true;
    }

    @Override
    public void releaseLock(String lockId) {}

    @Override
    public void deleteLock(String lockId) {}
}
