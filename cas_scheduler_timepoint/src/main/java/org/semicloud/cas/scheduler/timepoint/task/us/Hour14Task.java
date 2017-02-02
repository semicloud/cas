package org.semicloud.cas.scheduler.timepoint.task.us;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.semicloud.cas.model.BaseModel;
import org.semicloud.cas.scheduler.timepoint.task.BaseTask;
import org.semicloud.cas.scheduler.timepoint.task.TimePoint;
import org.semicloud.cas.shared.BasicEqEvent;

import java.util.Date;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 14小时研判任务
 */
public class Hour14Task extends BaseTask implements Job {

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        buildMDC(context);
        BasicEqEvent event = (BasicEqEvent) context.getJobDetail().getJobDataMap().get("BasicEQEvent");
        log.info(text("{0},mag:{1} hour14 analysis task startup @ {2}", event.getInfo(), event.getMagnitude(),
                new Date()));
        for (BaseModel m : invoker.getModels(TimePoint.HOUR_14, event.getEqID())) {
            if (m != null) {
                if (m.process()) {
                    log.info(text("model {0} at {1} processed -> {2}", m.getName(), TimePoint.HOUR_14, true));
                }
            }
        }
    }
}
