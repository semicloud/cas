package org.semicloud.cas.scheduler.timepoint;

import com.j256.simplejmx.server.JmxServer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.cas.shared.intensity.oval.M01;
import org.semicloud.cas.shared.intensity.oval.M02;
import org.semicloud.cas.shared.intensity.oval.M03;
import org.semicloud.cas.shared.intensity.oval.M04;
import org.semicloud.utils.db.setting.DbSettings;

/**
 * The listener interface for receiving timePointScheduler events. The class
 * that is interested in processing a timePointScheduler event implements this
 * interface, and the object created with that class is registered with a
 * component using the component's
 * <code>addTimePointSchedulerListener<code> method. When
 * the timePointScheduler event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TimePointSchedulerEvent
 */
public class TimePointSchedulerListener implements SchedulerListener {

    /**
     * The _log.
     */
    private static Log _log = LogFactory.getLog(TimePointSchedulerListener.class);

    @Override
    public void jobAdded(JobDetail arg0) {
        _log.info("Job[" + arg0.getKey().getName() + "] add to scheduler");
    }

    @Override
    public void jobDeleted(JobKey arg0) {
        _log.info("Job[" + arg0.getName() + "] delete from scheduler");
    }

    @Override
    public void jobPaused(JobKey arg0) {

    }

    @Override
    public void jobResumed(JobKey arg0) {

    }

    @Override
    public void jobScheduled(Trigger arg0) {
        _log.info("Trigger [" + arg0.getKey().getName() + "] scheduled at [" + arg0.getNextFireTime() + "]");
    }

    @Override
    public void jobUnscheduled(TriggerKey arg0) {

    }

    @Override
    public void jobsPaused(String arg0) {

    }

    @Override
    public void jobsResumed(String arg0) {

    }

    @Override
    public void schedulerError(String arg0, SchedulerException arg1) {

    }

    @Override
    public void schedulerInStandbyMode() {

    }

    @Override
    public void schedulerShutdown() {

    }

    @Override
    public void schedulerShuttingdown() {

    }

    @Override
    public void schedulerStarted() {
        _log.info("Engine Started..");
        _log.info(StringUtils.center("DATABASE INFORMATION", 80, "-"));
        _log.info("Database:" + DbSettings.getDatabase());
        _log.info("JDBC_URL:" + DbSettings.getJdbcUrl());
        _log.info("DRIVER_CLASS:" + DbSettings.getDriverClassName());
        _log.info("USER:" + DbSettings.getUserName());
        _log.info(StringUtils.repeat("-", 80));

        _log.info("");

        _log.info(StringUtils.center("GIS INFORMATION", 80, "-"));
        _log.info("DB_DRIVER_NAME:" + Settings.getGisSettings().getDbDriverName());
        _log.info("DB_SERVER_NAME:" + Settings.getGisSettings().getDbServerName());
        _log.info("DB_DATABASE_NAME:" + Settings.getGisSettings().getDbDatabaseName());
        _log.info("DB_USER:" + Settings.getGisSettings().getDbUserName());
        _log.info("DB_PASSWORD:" + Settings.getGisSettings().getDbUserPassword());
        _log.info(StringUtils.repeat("-", 80));

        _log.info("SCAN INTERVAL:" + Settings.getTimePointSchedulerScanInterval() + "s");

        _log.info("Initlize MBean Server...");
        try {
            JmxServer server = new JmxServer(8000);
            server.start();
            server.register(new M01());
            server.register(new M02());
            server.register(new M03());
            server.register(new M04());
            _log.info("jmx server and web server started...the web server port is 9090...");
        } catch (Exception e) {
            _log.error("jmx server starts failed, err msg:" + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.SchedulerListener#schedulingDataCleared()
     */
    @Override
    public void schedulingDataCleared() {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.SchedulerListener#triggerFinalized(org.quartz.Trigger)
     */
    @Override
    public void triggerFinalized(Trigger arg0) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.SchedulerListener#triggerPaused(org.quartz.TriggerKey)
     */
    @Override
    public void triggerPaused(TriggerKey arg0) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.SchedulerListener#triggerResumed(org.quartz.TriggerKey)
     */
    @Override
    public void triggerResumed(TriggerKey arg0) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.SchedulerListener#triggersPaused(java.lang.String)
     */
    @Override
    public void triggersPaused(String arg0) {

    }

    /*
     * (non-Javadoc)
     *
     * @see org.quartz.SchedulerListener#triggersResumed(java.lang.String)
     */
    @Override
    public void triggersResumed(String arg0) {

    }
}
