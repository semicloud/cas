package org.semicloud.cas.alarm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.semicloud.cas.shared.cfg.Settings;
import org.semicloud.utils.common.Convert;
import org.semicloud.utils.common.ConvertSetting;

import java.net.Inet4Address;
import java.util.Date;
import java.util.Map.Entry;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 告警接收模块入口函数
 */
public class AlarmMain {

    /**
     * 日志对象
     */
    private static Log log = LogFactory.getLog(AlarmMain.class);

    /**
     * 启动告警接收模块
     */
    private static void startup() {
        try {
            String address = Inet4Address.getLocalHost().getHostAddress();
            log.info("CAS ALARM ENGINE STARTED @ "
                    + Convert.dateToString(new Date(), ConvertSetting.LONG_DATE_TIME_PATTERN));
            log.info("local address:" + address);

            Server server = new Server(Settings.getServerAlarmPort());
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            server.setHandler(context);

            context.addServlet(new ServletHolder(new DefaultServlet()), Settings.getServerAlarmPath());
            log.info("add default servlet, visit url:");
            log.info(text("http://{0}:{1}{2}", address, Settings.getServerAlarmPort(), Settings.getServerAlarmPath()));

            context.addServlet(new ServletHolder(new DomainXmlServlet()), "/");
            log.info("add root servlet, visit url:");
            log.info(text("http://{0}:{1}/", address, Settings.getServerAlarmPort()));
            server.start();

            log.info("waiting for alarm request from these clients:");
            for (Entry<Object, Object> client : Settings.getClientProperties().entrySet()) {
                log.info(text("\tclient name:{0}, address:{1}", client.getKey(), client.getValue()));
            }
            server.join();
        } catch (Exception ex) {
            log.info("ERROR!!!alarm server startup failed, error messages:" + ex.getMessage());
        }
    }

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        startup();
    }
}
