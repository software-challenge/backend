package sc.framework.plugins;

import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO We can probably utilise an inbuilt class instead.
/** Tracks timeouts in Milliseconds. */
public class ActionTimeout {
    static final Logger logger = LoggerFactory.getLogger(ActionTimeout.class);

    private final long softTimeoutInMilliseconds;

    private final long hardTimeoutInMilliseconds;

    private final boolean canTimeout;

    private Thread timeoutThread;

    private Status status = Status.NEW;

    private long startTimestamp = 0;
    private long stopTimestamp = 0;
    private long gcTime = 0;

    private static final int DEFAULT_HARD_TIMEOUT = 10000;
    private static final int DEFAULT_SOFT_TIMEOUT = 5000;

    private enum Status {
        NEW, STARTED, STOPPED
    }

    public ActionTimeout(boolean canTimeout) {
        this(canTimeout, DEFAULT_HARD_TIMEOUT, DEFAULT_SOFT_TIMEOUT);
    }

    public ActionTimeout(boolean canTimeout, int hardTimeoutInMilliseconds) {
        this(canTimeout, hardTimeoutInMilliseconds, hardTimeoutInMilliseconds);
    }

    public ActionTimeout(boolean canTimeout, int hardTimeoutInMilliseconds, int softTimeoutInMilliseconds) {
        if (hardTimeoutInMilliseconds < softTimeoutInMilliseconds) {
            throw new IllegalArgumentException(
                    "HardTimeout must be greater or equal the SoftTimeout");
        }

        this.canTimeout = canTimeout;
        this.softTimeoutInMilliseconds = softTimeoutInMilliseconds;
        this.hardTimeoutInMilliseconds = hardTimeoutInMilliseconds;
    }

    public long getHardTimeout() {
        return this.hardTimeoutInMilliseconds;
    }

    public long getSoftTimeout() {
        return this.softTimeoutInMilliseconds;
    }

    public boolean canTimeout() {
        return this.canTimeout;
    }

    public long getTimeDiff() {
        if (this.status == Status.NEW) {
            throw new IllegalStateException("Timeout was never started.");
        }

        if (this.status == Status.STARTED) {
            throw new IllegalStateException("Timeout was not stopped.");
        }

        // Subtract the garbage collection time for an unaltered time
        return stopTimestamp - startTimestamp - gcTime;
    }

    public synchronized boolean didTimeout() {
        return this.canTimeout() && this.getTimeDiff() > this.softTimeoutInMilliseconds;
    }

    public synchronized void stop() {
        if (this.status == Status.NEW) {
            throw new IllegalStateException("Timeout was never started.");
        }

        if (this.status == Status.STOPPED) {
            logger.warn("Redundant stop: Timeout was already stopped.");
            return;
        }

        this.stopTimestamp = System.currentTimeMillis();
        this.status = Status.STOPPED;

        if (this.timeoutThread != null) {
            this.timeoutThread.interrupt();
        }

        // Get the garbage collection time
        this.gcTime = ManagementFactory.getGarbageCollectorMXBeans()
                .stream()
                .mapToLong(GarbageCollectorMXBean::getCollectionTime)
                .sum();
    }

    public synchronized void start(final Runnable onTimeout) {
        if (this.status != Status.NEW) {
            throw new IllegalStateException("Redundant start: was already started!");
        }

        clearGCStats();

        if (canTimeout()) {
            this.timeoutThread = new Thread(() -> {
                try {
                    Thread.sleep(getHardTimeout());
                    stop();
                    onTimeout.run();
                } catch (InterruptedException e) {
                    logger.info("HardTimout wasn't reached.");
                }
            });
            this.timeoutThread.start();
        }

        this.startTimestamp = System.currentTimeMillis();
        this.status = Status.STARTED;
    }

    private void clearGCStats() {
        // With the call of `getCollectionTime` we'll clear the GarbageCollectorMXBean from older gcs.
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        gcBeans.forEach(GarbageCollectorMXBean::getCollectionTime);
    }
}