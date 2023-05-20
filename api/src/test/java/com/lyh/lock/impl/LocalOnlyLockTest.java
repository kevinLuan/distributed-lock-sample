package com.lyh.lock.impl;

import com.lyh.lock.config.LocalOnlyLockConfiguration;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class LocalOnlyLockTest {

    // Lock can be global since it uses global cache internally
    private final LocalOnlyLock localOnlyLock = new LocalOnlyLock();

    @Test
    public void testLockUnlock() {
        localOnlyLock.acquireLock("a", 100, 1000, TimeUnit.MILLISECONDS);
        assertEquals(localOnlyLock.cache().size(), 1);
        assertEquals(localOnlyLock.cache().getUnchecked("a").availablePermits(), 0);
        assertEquals(localOnlyLock.scheduledFutures().size(), 1);
        localOnlyLock.releaseLock("a");
        assertEquals(localOnlyLock.scheduledFutures().size(), 0);
        assertEquals(localOnlyLock.cache().getUnchecked("a").availablePermits(), 1);
        localOnlyLock.deleteLock("a");
        assertEquals(localOnlyLock.cache().size(), 0);
    }

    @Test(timeout = 10 * 1000)
    public void testLockTimeout() {
        localOnlyLock.acquireLock("c", 100, 1000, TimeUnit.MILLISECONDS);
        assertTrue(localOnlyLock.acquireLock("d", 100, 1000, TimeUnit.MILLISECONDS));
        assertFalse(localOnlyLock.acquireLock("c", 100, 1000, TimeUnit.MILLISECONDS));
        assertEquals(localOnlyLock.scheduledFutures().size(), 2);
        localOnlyLock.releaseLock("c");
        localOnlyLock.releaseLock("d");
        assertEquals(localOnlyLock.scheduledFutures().size(), 0);
    }

    @Test(timeout = 10 * 1000)
    public void testLockLeaseTime() {
        for (int i = 0; i < 10; i++) {
            localOnlyLock.acquireLock("a", 1000, 100, TimeUnit.MILLISECONDS);
        }
        localOnlyLock.acquireLock("a");
        assertEquals(0, localOnlyLock.cache().getUnchecked("a").availablePermits());
        localOnlyLock.releaseLock("a");
    }

    @Test(timeout = 10 * 1000)
    public void testLockLeaseWithRelease() throws Exception {
        localOnlyLock.acquireLock("b", 1000, 1000, TimeUnit.MILLISECONDS);
        localOnlyLock.releaseLock("b");

        // Wait for lease to run out and also call release
        Thread.sleep(2000);

        localOnlyLock.acquireLock("b");
        assertEquals(0, localOnlyLock.cache().getUnchecked("b").availablePermits());
        localOnlyLock.releaseLock("b");
    }

    @Test
    public void testRelease() {
        localOnlyLock.releaseLock("x54as4d2;23'4");
        localOnlyLock.releaseLock("x54as4d2;23'4");
        assertEquals(1, localOnlyLock.cache().getUnchecked("x54as4d2;23'4").availablePermits());
    }

    @Test
    public void testLockConfiguration() {
        new ApplicationContextRunner()
                .withPropertyValues("distributed.lock.type=local_only")
                .withUserConfiguration(LocalOnlyLockConfiguration.class)
                .run(context -> {
                    LocalOnlyLock lock = context.getBean(LocalOnlyLock.class);
                    assertNotNull(lock);
                });
    }
}
