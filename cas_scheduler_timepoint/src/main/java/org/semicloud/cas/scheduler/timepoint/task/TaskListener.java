package org.semicloud.cas.scheduler.timepoint.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.semicloud.utils.db.BaseDao;
import org.semicloud.utils.db.factory.DaoFactory;

/**
 * The listener interface for receiving task events. The class that is
 * interested in processing a task event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addTaskListener<code> method. When
 * the task event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TaskEvent
 */
public class TaskListener implements JobListener {

    /**
     * The Constant DAO.
     */
    private static final BaseDao DAO = DaoFactory.getInstance();
    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(TaskListener.class);

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.JobListener#getName()
     */
    @Override
    public String getName() {
        return "CAS.TASKLISTENER Ver 1.0 COPYRIGT(R) CSS. CO. LTD 2009-2013";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.quartz.JobListener#jobExecutionVetoed(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        BaseTask.buildMDC(context);
        log.error(context.getJobDetail().getKey().getName() + " is Vetoed by Trigger.");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.quartz.JobListener#jobToBeExecuted(org.quartz.JobExecutionContext)
     */
    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        BaseTask.buildMDC(context);
        log.info(context.getJobDetail().getKey().getName() + " analysis bengin.");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.quartz.JobListener#jobWasExecuted(org.quartz.JobExecutionContext,
     * org.quartz.JobExecutionException)
     */
    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException exception) {
        BaseTask.buildMDC(context);
        String cmd = "INSERT INTO event_state(phase) VALUES (?)";
        boolean stored = DAO.update(cmd, context.getJobDetail().getKey().getName());
        if (stored)
            log.info(context.getJobDetail().getKey().getName() + "analysis complete.");
        else {
            log.error(context.getJobDetail().getKey().getName() + "mark process failed.");
        }
    }
}
