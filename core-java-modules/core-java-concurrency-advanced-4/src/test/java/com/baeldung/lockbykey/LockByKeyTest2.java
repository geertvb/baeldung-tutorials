package com.baeldung.lockbykey;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static com.baeldung.lockbykey.ThreadUtils.run;
import static com.baeldung.lockbykey.ThreadUtils.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
class LockByKeyTest2 {

    public static final String LOCK_KEY = "AAA";

    CountDownLatch countDownLatch;
    LockByKey lockByKey = new LockByKey();

    protected void first() {
        lockByKey.lock(LOCK_KEY);
        log.info("Processing first");
        sleep(1000);
        log.info("Done first");
        lockByKey.unlock(LOCK_KEY);
        countDownLatch.countDown();
    }

    protected void second() {
        lockByKey.lock(LOCK_KEY);
        log.info("Processing second");
        sleep(1000);
        log.info("Done second");
        lockByKey.unlock(LOCK_KEY);
        countDownLatch.countDown();
    }

    protected void third() {
        lockByKey.lock(LOCK_KEY);
        log.info("Processing third");
        sleep(1000);
        log.info("Done third");
        lockByKey.unlock(LOCK_KEY);
        countDownLatch.countDown();
    }


    /**
     * Starts a second thread that locks the same key after the first thread is removed from the thread queue,
     * and a third thread after the key is removed from the map.
     * The second thread will run but will throw a NPE when trying to unlock.
     * The third thread will add a new lock to the map and will run concurrently with the second thread.
     * <pre>
     * 13:25:11.549 [Thread-0] INFO com.baeldung.lockbykey.LockByKeyTest2 - Processing first
     * 13:25:12.553 [Thread-0] INFO com.baeldung.lockbykey.LockByKeyTest2 - Done first
     * 13:25:12.795 [Thread-1] INFO com.baeldung.lockbykey.LockByKeyTest2 - Processing second
     * 13:25:13.295 [Thread-2] INFO com.baeldung.lockbykey.LockByKeyTest2 - Processing third
     * 13:25:13.795 [Thread-1] INFO com.baeldung.lockbykey.LockByKeyTest2 - Done second
     * Exception in thread "Thread-1" java.lang.IllegalMonitorStateException
     * 	at java.base/java.util.concurrent.locks.ReentrantLock$Sync.tryRelease(ReentrantLock.java:149)
     * 	at java.base/java.util.concurrent.locks.AbstractQueuedSynchronizer.release(AbstractQueuedSynchronizer.java:1302)
     * 	at java.base/java.util.concurrent.locks.ReentrantLock.unlock(ReentrantLock.java:439)
     * 	at com.baeldung.lockbykey.LockByKey.unlock(LockByKey.java:36)
     * 	at com.baeldung.lockbykey.LockByKeyTest2.second(LockByKeyTest2.java:32)
     * 	at java.base/java.lang.Thread.run(Thread.java:829)
     * 13:25:14.296 [Thread-2] INFO com.baeldung.lockbykey.LockByKeyTest2 - Done third
     * </pre>
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        countDownLatch = new CountDownLatch(3);
        run(this::first);
        sleep(1250);
        run(this::second);
        sleep(500);
        run(this::third);
        countDownLatch.await(5, SECONDS);
    }

}