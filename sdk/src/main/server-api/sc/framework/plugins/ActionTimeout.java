package sc.framework.plugins;

import java.lang.management.ManagementFactory;
import java.lang.management.GarbageCollectorMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private long gcStartTime = 0;
    private long gcStopTime = 0;

    private static final int DEFAULT_HARD_TIMEOUT = 10000;
    private static final int DEFAULT_SOFT_TIMEOUT = 2000;

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


        logger.info("Garbage collection time: {} {} {}", gcStopTime, gcStartTime, gcStopTime - gcStartTime);
        logger.info("Real time difference: {}", stopTimestamp - startTimestamp);
        logger.info("Unaltered time difference: {}", stopTimestamp - startTimestamp - gcStopTime + gcStartTime);
        // Subtract the garbage collection time for an unaltered time
        return stopTimestamp - startTimestamp - gcStopTime + gcStartTime;
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
        this.gcStopTime = getGCTime();
    }

    public synchronized void start(final Runnable onTimeout) {
        if (this.status != Status.NEW) {
            throw new IllegalStateException("Redundant start: was already started!");
        }

        if (canTimeout()) {
            this.timeoutThread = new Thread(() -> {
                try {
                    Thread.sleep(getHardTimeout());
                    stop();
                    onTimeout.run();
                } catch (InterruptedException e) {
                    logger.info("HardTimeout wasn't reached.");
                }
            });

            // Get the garbage collection time
            this.gcStartTime = getGCTime();
            this.timeoutThread.start();
        }

        this.startTimestamp = System.currentTimeMillis();
        this.status = Status.STARTED;
    }

  /**
   * Measures the total garbage collection time.
   * This is meant to be used as a value relative to a start time.
   * @return The total garbage collection time
   */
  private long getGCTime() {
        long gcTime = 0;
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            gcTime += gcBean.getCollectionTime();
        }
        return gcTime;
    }

    @Override
    public String toString() {
        return "ActionTimeout{" +
            "canTimeout=" + canTimeout +
            ", status=" + status +
            ", start=" + startTimestamp +
            ", stop=" + stopTimestamp +
            ", gcStart=" + gcStartTime +
            ", gcStop=" + gcStopTime +
            '}';
    }
}