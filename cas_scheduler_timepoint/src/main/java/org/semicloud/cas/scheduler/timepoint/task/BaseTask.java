package org.semicloud.cas.scheduler.timepoint.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.quartz.JobExecutionContext;
import org.semicloud.cas.scheduler.timepoint.ModelInvoker;

// TODO: Auto-generated Javadoc

/**
 * 研判任务基类.
 *
 * @author Victor
 */
public abstract class BaseTask {

    /**
     * The invoker.
     */
    protected static ModelInvoker invoker = new ModelInvoker();
    /**
     * The log.
     */
    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * Builds the mdc,用来向log4j组件发送消息
     *
     * @param context the context
     */
    protected static void buildMDC(JobExecutionContext context) {
        MDC.put("TASK_ID", context.getJobDetail().getKey().getName());
        MDC.put("EQ_ID", context.getJobDetail().getKey().getGroup());
    }
}
