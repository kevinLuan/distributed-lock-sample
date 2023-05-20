# 分布式锁实现

## redis 实现分布式锁

    distributed.lock.type=redis
    distributed.redis-lock.serverAddress=redis://127.0.0.1:6379
    distributed.redis-lock.serverPassword=

## zookeeper 实现分布式锁

    distributed.lock.type=zookeeper 
    distributed.zookeeper-lock.connectionString=localhost:2181

## 实现本地锁
    distributed.lock.type=local_only

## 演示示例
```java
    @Autowired
    private Lock lock;

    public void test() {
        String lockId = "MyLockId";
        if (lock.acquireLock(lockId, 5, TimeUnit.MILLISECONDS)) {
           // do work
        } finally {
            lock.releaseLock(lockId)
        }
    }
```