package org.semicloud.cas.scheduler.timepoint;

/**
 * 入口
 */
public class TimePointSchedulerMain {

    /**
     * The scheduler.
     */
    private static TimePointScheduler scheduler = TimePointScheduler.getInstance();

    /**
     * The main method.
     * b11_nerss
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        Thread worker = new Thread(scheduler);
        worker.start();
    }
}
