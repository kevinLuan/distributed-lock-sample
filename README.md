# 分布式锁实现

## redis

    distributed.lock.type=redis
    distributed.redis-lock.serverAddress=redis://127.0.0.1:6379
    distributed.redis-lock.serverPassword=

## zookeeper

    distributed.lock.type=zookeeper 
    distributed.zookeeper-lock.connectionString=localhost:2181

## 本地锁
    distributed.lock.type=local_only

## 实现示例
```java
    @Autowired
    private Lock lock;

    public void test() {
        String lockId = "lockKey";
        if (lock.acquireLock(lockId, 5, TimeUnit.MILLISECONDS)) {
           // do work
        } finally {
            lock.releaseLock(workflowId)
        }
    }
```