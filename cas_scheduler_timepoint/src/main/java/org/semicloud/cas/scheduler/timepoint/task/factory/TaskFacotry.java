package org.semicloud.cas.scheduler.timepoint.task.factory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.DateBuilder.IntervalUnit;
import org.semicloud.cas.scheduler.timepoint.task.TaskType;
import org.semicloud.cas.scheduler.timepoint.task.TriggerType;
import org.semicloud.cas.scheduler.timepoint.task.us.*;
import org.semicloud.cas.shared.BasicEqEvent;
import org.semicloud.cas.shared.cfg.Settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 任务工厂类.
 *
 * @author Victor
 */
public class TaskFacotry {
    /**
     * 日志对象
     */
    private static Log log = LogFactory.getLog(TaskFacotry.class);

    /**
     * 根据地震事件获得任务(Job).
     *
     * @param event 地震事件
     * @return Map<JobDetail,List<Trigger>> 每一个JobDetail对应若干Trigger
     */
    public static Map<JobDetail, List<Trigger>> getTask(BasicEqEvent event) {
        Map<JobDetail, List<Trigger>> ret = new HashMap<>();
        if (event != null) {
            // 获取JobDetail
            ArrayList<JobDetail> jobDetails = getJobDetailList(event);
            // 获取Trigger
            ArrayList<Trigger> triggers = getTriggerList(event);
            // 如果JobDetail的个数和Trigger的个数不一致就报错
            if (jobDetails.size() != triggers.size()) {
                log.error("There's not match between job number and trigger number.");
            } else {
                // 将JobDetail与Trigger绑定
                for (int i = 0; i < jobDetails.size(); i++) {
                    List<Trigger> trigger = new ArrayList<>();
                    trigger.add(triggers.get(i));
                    ret.put(jobDetails.get(i), trigger);
                }
            }
        } else {
            // 地震事件为空，则报错
            log.error("Entity can not be null.");
            throw new IllegalArgumentException("entity can not be null.");
        }
        return ret;
    }

    /**
     * 将地震事件封装为JobDetail对象
     *
     * @param event the event
     * @return the job detail list
     */
    private static ArrayList<JobDetail> getJobDetailList(BasicEqEvent event) {
        ArrayList<JobDetail> ret = new ArrayList<>();
        // 获取jobDataMap
        JobDataMap jobDataMap = getJobDataMap(event);

        // 速判JobDetail
        JobDetail immeTask = JobBuilder.newJob(IMMETask.class).usingJobData(jobDataMap).requestRecovery(true)
                .withIdentity(getJobKey(event, TaskType._IMME)).build();
        ret.add(immeTask);

        // 30分钟JobDetail
        JobDetail min30Task = JobBuilder.newJob(Min30Task.class).usingJobData(jobDataMap).requestRecovery(true)
                .withIdentity(getJobKey(event, TaskType._MIN_30)).build();
        ret.add(min30Task);

        // 1小时JobDetail
        JobDetail hour1Task = JobBuilder.newJob(Hour1Task.class).usingJobData(jobDataMap).requestRecovery(true)
                .withIdentity(getJobKey(event, TaskType._HOUR_1)).build();
        ret.add(hour1Task);

        // 3小时JobDetail
        JobDetail hour3Task = JobBuilder.newJob(Hour3Task.class).usingJobData(jobDataMap).requestRecovery(true)
                .withIdentity(getJobKey(event, TaskType._HOUR_3)).build();
        ret.add(hour3Task);

        // 6小时JobDetail
        JobDetail hour6Task = JobBuilder.newJob(Hour6Task.class).usingJobData(jobDataMap).requestRecovery(true)
                .withIdentity(getJobKey(event, TaskType._HOUR_6)).build();
        ret.add(hour6Task);

        // 10小时JobDeitail
        JobDetail hour10Task = JobBuilder.newJob(Hour10Task.class).usingJobData(jobDataMap).requestRecovery(true)
                .withIdentity(getJobKey(event, TaskType._HOUR_10)).build();
        ret.add(hour10Task);

        // 14小时JobDetail
        JobDetail hour14Task = JobBuilder.newJob(Hour14Task.class).usingJobData(jobDataMap).requestRecovery(true)
                .withIdentity(getJobKey(event, TaskType._HOUR_14)).build();
        ret.add(hour14Task);

        return ret;
    }

    /**
     * 获得触发器Trigger列表.
     *
     * @param event 地震事件列表
     * @return the trigger list
     */
    private static ArrayList<Trigger> getTriggerList(BasicEqEvent event) {
        ArrayList<Trigger> ret = new ArrayList<>();

        // 速判触发器
        Trigger immeTrigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(event, TriggerType._IMME_TASK_TRIGGER)).withPriority(1)
                .startAt(DateBuilder.futureDate(Settings.getTimePointImme(), IntervalUnit.SECOND)).build();
        log.info(text("{0}{1} immediate analysis will triggered @ {2}", event.getInfo(), event.getMagnitude(),
                DateBuilder.futureDate(Settings.getTimePointImme(), IntervalUnit.SECOND)));
        ret.add(immeTrigger);

        // 30分钟触发器
        Trigger min30Trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(event, TriggerType._MIN_30_TASK_TRIGGER)).withPriority(1)
                .startAt(DateBuilder.futureDate(Settings.getTimePointMin30(), IntervalUnit.SECOND)).build();
        log.info(text("{0}{1} min30 analysis will triggered @ {2}", event.getInfo(), event.getMagnitude(),
                DateBuilder.futureDate(Settings.getTimePointMin30(), IntervalUnit.SECOND)));
        ret.add(min30Trigger);

        // 1小时触发器
        Trigger hour1Trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(event, TriggerType._HOUR_1_TASK_TRIGGER)).withPriority(2)
                .startAt(DateBuilder.futureDate(Settings.getTimePointHour1(), IntervalUnit.SECOND)).build();
        log.info(text("{0}{1} hour1 analysis will triggered @ {2}", event.getInfo(), event.getMagnitude(),
                DateBuilder.futureDate(Settings.getTimePointHour1(), IntervalUnit.SECOND)));
        ret.add(hour1Trigger);

        // 3小时触发器
        Trigger hour3Trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(event, TriggerType._HOUR_3_TASK_TRIGGER)).withPriority(3)
                .startAt(DateBuilder.futureDate(Settings.getTimePointHour3(), IntervalUnit.SECOND)).build();
        log.info(text("{0}{1} hour3 analysis will triggered @ {2}", event.getInfo(), event.getMagnitude(),
                DateBuilder.futureDate(Settings.getTimePointHour3(), IntervalUnit.SECOND)));
        ret.add(hour3Trigger);

        // 6小时触发器
        Trigger hour6Trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(event, TriggerType._HOUR_6_TASK_TRIGGER)).withPriority(4)
                .startAt(DateBuilder.futureDate(Settings.getTimePointHour6(), IntervalUnit.SECOND)).build();
        log.info(text("{0}{1} hour6 analysis will triggered @ {2}", event.getInfo(), event.getMagnitude(),
                DateBuilder.futureDate(Settings.getTimePointHour6(), IntervalUnit.SECOND)));
        ret.add(hour6Trigger);

        // 10小时触发器
        Trigger hour10Trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(event, TriggerType._HOUR_10_TASK_TRIGGER)).withPriority(5)
                .startAt(DateBuilder.futureDate(Settings.getTimePointHour10(), IntervalUnit.SECOND)).build();
        log.info(text("{0}{1} hour10 analysis will triggered @ {2}", event.getInfo(), event.getMagnitude(),
                DateBuilder.futureDate(Settings.getTimePointHour10(), IntervalUnit.SECOND)));
        ret.add(hour10Trigger);

        // 14小时触发器
        Trigger hour14Trigger = TriggerBuilder.newTrigger()
                .withIdentity(getTriggerKey(event, TriggerType._HOUR_14_TASK_TRIGGER)).withPriority(6)
                .startAt(DateBuilder.futureDate(Settings.getTimePointHour14(), IntervalUnit.SECOND)).build();
        log.info(text("{0}{1} hour14 analysis will triggered @ {2}", event.getInfo(), event.getMagnitude(),
                DateBuilder.futureDate(Settings.getTimePointHour14(), IntervalUnit.SECOND)));
        ret.add(hour14Trigger);
        return ret;
    }

    /**
     * 获得JobKey.
     *
     * @param event    地震事件
     * @param taskType 任务类型
     * @return the job key
     */
    private static JobKey getJobKey(BasicEqEvent event, TaskType taskType) {
        JobKey key = null;
        String eqID = event.getEqID();
        if (eqID.length() == 27) {
            switch (taskType) {
                case _IMME:
                    key = new JobKey(eqID + TaskType._IMME.name(), eqID);
                    break;
                case _MIN_30:
                    key = new JobKey(eqID + TaskType._MIN_30.name(), eqID);
                    break;
                case _HOUR_1:
                    key = new JobKey(eqID + TaskType._HOUR_1.name(), eqID);
                    break;
                case _HOUR_3:
                    key = new JobKey(eqID + TaskType._HOUR_3.name(), eqID);
                    break;
                case _HOUR_6:
                    key = new JobKey(eqID + TaskType._HOUR_6.name(), eqID);
                    break;
                case _HOUR_10:
                    key = new JobKey(eqID + TaskType._HOUR_10.name(), eqID);
                    break;
                case _HOUR_14:
                    key = new JobKey(eqID + TaskType._HOUR_14.name(), eqID);
                    break;
                default:
                    break;
            }
        } else {
            System.err.println("EQ_ID Resolve Failure.");
        }
        return key;
    }

    /**
     * 获得触发器Key.
     *
     * @param event       地震事件
     * @param triggerType 触发器类型
     * @return the trigger key
     */
    private static TriggerKey getTriggerKey(BasicEqEvent event, TriggerType triggerType) {
        TriggerKey key = null;
        String eqID = event.getEqID();
        if (eqID.length() == 27) {
            switch (triggerType) {
                case _IMME_TASK_TRIGGER:
                    key = new TriggerKey(eqID + TriggerType._IMME_TASK_TRIGGER.name(), eqID);
                    break;
                case _MIN_30_TASK_TRIGGER:
                    key = new TriggerKey(eqID + TriggerType._MIN_30_TASK_TRIGGER.name(), eqID);
                    break;
                case _HOUR_1_TASK_TRIGGER:
                    key = new TriggerKey(eqID + TriggerType._HOUR_1_TASK_TRIGGER.name(), eqID);
                    break;
                case _HOUR_3_TASK_TRIGGER:
                    key = new TriggerKey(eqID + TriggerType._HOUR_3_TASK_TRIGGER.name(), eqID);
                    break;
                case _HOUR_6_TASK_TRIGGER:
                    key = new TriggerKey(eqID + TriggerType._HOUR_6_TASK_TRIGGER.name(), eqID);
                    break;
                case _HOUR_10_TASK_TRIGGER:
                    key = new TriggerKey(eqID + TriggerType._HOUR_10_TASK_TRIGGER.name(), eqID);
                    break;
                case _HOUR_14_TASK_TRIGGER:
                    key = new TriggerKey(eqID + TriggerType._HOUR_14_TASK_TRIGGER.name(), eqID);
                    break;
                default:
                    break;
            }
        } else
            System.err.println("EQ_ID Resolve ");
        return key;
    }

    /**
     * 获取JobDataMap，用于封装地震事件的数据，传递给调度器
     *
     * @param event the event
     * @return the job data map
     */
    private static JobDataMap getJobDataMap(BasicEqEvent event) {
        Map<String, BasicEqEvent> map = new HashMap<>();
        map.put("BasicEQEvent", event);
        return new JobDataMap(map);
    }
}
