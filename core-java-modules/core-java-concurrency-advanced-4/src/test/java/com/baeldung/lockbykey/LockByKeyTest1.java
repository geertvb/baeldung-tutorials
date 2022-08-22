package com.baeldung.lockbykey;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

import static com.baeldung.lockbykey.ThreadUtils.run;
import static com.baeldung.lockbykey.ThreadUtils.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
class LockByKeyTest1 {

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

    /**
     * Starts a second thread that locks the same key after the first thread is removed from the thread queue
     * and before the key is removed from the ConcurrentHashMap that contains the locks.
     * The second thread will run but will throw a NPE when trying to unlock.
     * <pre>
     * 13:20:57.505 [Thread-0] INFO com.baeldung.lockbykey.LockByKeyTest1 - Processing first
     * 13:20:58.509 [Thread-0] INFO com.baeldung.lockbykey.LockByKeyTest1 - Done first
     * 13:20:58.750 [Thread-1] INFO com.baeldung.lockbykey.LockByKeyTest1 - Processing second
     * 13:20:59.751 [Thread-1] INFO com.baeldung.lockbykey.LockByKeyTest1 - Done second
     * Exception in thread "Thread-1" java.lang.NullPointerException
     * 	at com.baeldung.lockbykey.LockByKey$LockWrapper.access$000(LockByKey.java:12)
     * 	at com.baeldung.lockbykey.LockByKey.unlock(LockByKey.java:36)
     * 	at com.baeldung.lockbykey.LockByKeyTest1.second(LockByKeyTest1.java:34)
     * 	at java.base/java.lang.Thread.run(Thread.java:829)
     * 	</pre>
     *
     * @throws InterruptedException
     */
    @Test
    public void test() throws InterruptedException {
        countDownLatch = new CountDownLatch(2);
        run(this::first);
        sleep(1250);
        run(this::second);
        countDownLatch.await(5, SECONDS);
    }

}