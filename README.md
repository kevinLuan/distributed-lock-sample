# Multiple api implementations of distributed locks

## Distributed locking through redis
    /** Support redis simple mode, cluster mode, sentinel mode **/

    distributed.lock.type=redis
    distributed.redis-lock.serverAddress=redis://127.0.0.1:6379
    distributed.redis-lock.serverPassword=

## Distributed locking through zookeeper
    distributed.lock.type=zookeeper 
    distributed.zookeeper-lock.connectionString=localhost:2181

## Implement local locks
    distributed.lock.type=local_only

## Demo sample program
```java
    // Interface implemented by a distributed lock client.
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