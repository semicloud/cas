package org.semicloud.cas.scheduler.timepoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.semicloud.cas.model.al.ModelGal;
import org.semicloud.cas.scheduler.timepoint.selector.SelectorType;
import org.semicloud.cas.scheduler.timepoint.selector.TaskSelectorContext;
import org.semicloud.cas.scheduler.timepoint.task.TaskListener;
import org.semicloud.cas.scheduler.timepoint.task.factory.TaskFacotry;
import org.semicloud.cas.shared.BasicEqEvent;
import org.semicloud.cas.shared.EpiCenter;
import org.semicloud.cas.shared.al.SharedGal;
import org.semicloud.cas.shared.cfg.Settings;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 应用研判模型时间点调度器
 */
public class TimePointScheduler implements Runnable {
    /**
     * 研判任务扫描时间间隔
     */
    private static final Integer SCAN_INTERVAL = Settings.getTimePointSchedulerScanInterval() * 1000;
    /**
     * 生成MyScheduler对象，做单例
     */
    private static TimePointScheduler myScheduler = new TimePointScheduler();
    /**
     * Quartz Scheduler对象
     */
    private static Scheduler scheduler;
    /**
     * 任务选择器上下文
     */
    private static TaskSelectorContext context;
    /**
     * The _log.
     */
    private static Log log = LogFactory.getLog(TimePointScheduler.class);

    /**
     * 私有构造函数
     */
    private TimePointScheduler() {
        try {
            // 获得调度器工厂
            new StdSchedulerFactory();
            // 获取默认调度器
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            // 初始化选择器，选择类型为简单选择器
            context = new TaskSelectorContext(SelectorType.SimpleSelector);
            // 调度器加入Job Listener
            scheduler.getListenerManager().addJobListener(new TaskListener());
            // 调度器加入Scheduler Listener
            scheduler.getListenerManager().addSchedulerListener(new TimePointSchedulerListener());
            // 启动调度器
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单例
     *
     * @return single instance of TimePointScheduler
     */
    public static TimePointScheduler getInstance() {
        return myScheduler;
    }

    /**
     * 验证一个地震是否需要计算
     *
     * @param event
     * @return
     */
    private static boolean willCompute(BasicEqEvent event) {
        if (event.getDepth() > 1000.0) {
            log.info("depth greater than 300KM, analysis canceled.");
            return false;
        }
        boolean answer = false;
        float lng = event.getLongitude(), lat = event.getLatitude();
        double mag = event.getMagnitude();
        double nationalMagnitudeThreshold = Settings.getNationalMagnitudeThreshold();
        double internaionalMagnitudeThreshold = Settings.getInternationalMagnitudeThreshold();
        double oceanMagnitudeThreshold = Settings.getOceanMagnitudeThreshold();
        boolean hasCountry = ModelGal.hasCountry(new EpiCenter(lng, lat));
        String ab = SharedGal.getCountryName(lng, lat);
        if (hasCountry) {
            if (ab.equals("CN")) {
                answer = mag >= nationalMagnitudeThreshold ? true : false;
                log.info(text("震中位置位于国内，震级为{0}，是否展开研判{1}", mag, booleanToString(answer)));
            } else {
                answer = mag >= internaionalMagnitudeThreshold ? true : false;
                log.info(text("震中位于国外{0}，震级为{1}，是否展开研判{2}", ab, mag, booleanToString(answer)));
            }
        } else {
            answer = mag >= oceanMagnitudeThreshold ? true : false;
            log.info(text("震中位于海中，震级为{0}，是否展开研判{1}", mag, booleanToString(answer)));
        }
        return answer;
    }

    private static String booleanToString(boolean b) {
        if (b)
            return "，展开研判";
        else
            return "，不展开研判";
    }

    /**
     * 获取地震事件
     *
     * @return the event
     */
    private static BasicEqEvent getEvent() {
        BasicEqEvent event = context.selectLatest();
        if (event != null) {
            log.info("find basic_eq_event to schedule,");
            log.info("eqID:" + event.getEqID());
            log.info("lng,lat:" + event.getLongitude() + "," + event.getLatitude());
            log.info("mag:" + event.getMagnitude());
            log.info("depth:" + event.getDepth());
            log.info("time:" + event.getDateTime());
            log.info("info:" + event.getInfo());
            log.info("src:" + event.getSource());
        }
        return event != null ? event : null;
    }

    /**
     * 调度任务.
     *
     * @param event 要调度的地震事件
     */
    private static void scheduleTask(BasicEqEvent event) {
        try {
            // TODO 地震用户初始化，由于数据库异常，暂时不初始化
            // Initilizer.init(event.getEqID());
            // 将任务的处理状态改为已处理
            if (event.markProcess()) {
                // 调用调度器调度任务
                scheduler.scheduleJobs(TaskFacotry.getTask(event), false);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭调度器
     */
    public void shutdown() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            BasicEqEvent event = getEvent();
            if (event != null) {
                // 手动触发的不管震级多少都触发
                if (event.getSource() == "manual_web") {
                    log.info("手动触发，立即开始研判");
                    event.markProcess();
                    event.markIsAnalysis(true);
                    scheduleTask(event);
                } else {
                    if (!willCompute(event)) {
                        event.markIsAnalysis(false);
                        log.info(event.getDescription() + " 跳过");
                        event.markProcess();
                        continue;
                    }
                    // 调度该地震事件
                    event.markIsAnalysis(true);
                    scheduleTask(event);
                }

            } else {
                // 否则就睡了
                sleeping();
            }
        }
    }

    /**
     * Sleeping.
     */
    private void sleeping() {
        try {
            Thread.sleep(SCAN_INTERVAL);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
