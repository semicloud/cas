package org.semicloud.cas.alarm;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.semicloud.cas.alarm.processor.ClientProcessor;
import org.semicloud.cas.alarm.processor.ClientProcessorFactory;
import org.semicloud.cas.shared.cfg.Settings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static org.semicloud.utils.common.MyStringUtils.text;

/**
 * 用来接收请求的Servlet.
 *
 * @author Semicloud
 */
public class DefaultServlet extends HttpServlet {

    /**
     * 告警与速判系统名称，应与arm.ini配置文件中一致
     */
    public static final String ON_DUTY_CLIENT = "on-duty";
    /**
     * 人工编辑系统名称，应与arm.ini配置文件中一致
     */
    public static final String SPECLIST_EDIT_CLIENT = "speclist-edit";
    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The log.
     */
    private static Log log = LogFactory.getLog(DefaultServlet.class);

    /**
     * 从HttpServletRequest对象中解析出客户端名称
     *
     * @param req HttpServletRequest
     * @return 客户端名称
     */
    private static String getClient(HttpServletRequest req) {
        String address = req.getRemoteAddr();
        if (req.getParameter("client") != null) {
            return "speclist-edit";
        }
        String client = Settings.getAlarmClient(address);
        if (client.equals(StringUtils.EMPTY)) {
            if (StringUtils.isNotBlank(req.getParameter("client")))
                client = req.getParameter("client").split("[?]")[0];
        }
        log.info("client:" + client);
        return client;
    }

    /**
     * 打印接收到的告警消息
     *
     * @param client 客户端名称
     * @param req    HttpServletRequest
     */
    private static void printAlarmInfo(String client, HttpServletRequest req) {
        if (client == ON_DUTY_CLIENT) {
            log.info("this alarm is a on_duty alarm, the information as follows:");
            log.info("evtId:" + req.getParameter("evtId"));
            log.info("info:" + req.getParameter("info"));
            log.info("lng:" + req.getParameter("longitude"));
            log.info("lat:" + req.getParameter("latitude"));
            log.info("mag:" + req.getParameter("magnitude"));
            log.info("time:" + prettyTime(req.getParameter("evtId").substring(13)));
        }
        if (client == SPECLIST_EDIT_CLIENT) {
            log.info("this alarm is a speclist_edit alarm, information as follows:");
            log.info("eqID:" + req.getParameter("eqID"));
            log.info("taskID:" + req.getParameter("oldTaskID"));
            //log.info("Oh~,no...the taskID should be:" + req.getParameter("taskID").split("[?]")[0]);
        }
    }

    /**
     * 获取日期的字符串表示
     *
     * @param str the str
     * @return the string
     */
    private static String prettyTime(String str) {
        StringBuffer sb = new StringBuffer(str);
        sb.insert(4, "年");
        sb.insert(7, "月");
        sb.insert(10, "日");
        sb.insert(13, "时");
        sb.insert(16, "分");
        sb.insert(19, "秒");
        return sb.toString();
    }

    /*
     * doGet()
     *
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();

        String address = req.getRemoteAddr();
        String client = getClient(req);
        log.info(StringUtils.center(text("receive alarm from {0}({1})", client, address), 80, "-"));
        printAlarmInfo(client, req);

        if (StringUtils.isEmpty(client))
            return;
        ClientProcessor processor = ClientProcessorFactory.getProcessor(client);
        log.info(text("get processor {0} -> {1}", client, processor != null));
        if (processor.process(req, resp)) {
            log.info(text("alarm from {0}({1}) already processed success", client, address));
            if (client.equals(ON_DUTY_CLIENT))
                out.println("OK");
            if (client.equals(SPECLIST_EDIT_CLIENT))
                out.print("重计算请求已接受，开始重计算....");
            out.flush();
        } else {
            log.error("ERROR!!!!!alarm from {0}({1}) has been processed failed");
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "database has some internal errors");
        }

        log.info(StringUtils.center("process complete", 80, "-"));
    }

    /*
     * doPost()
     *
     * @see
     * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
     * , javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

}
