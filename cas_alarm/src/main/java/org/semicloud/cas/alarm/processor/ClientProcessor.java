package org.semicloud.cas.alarm.processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 消息处理器基类
 */
public abstract class ClientProcessor {

    /**
     * The err msg builder.
     */
    private StringBuilder errMsgBuilder = new StringBuilder();

    /**
     * 处理消息
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @return true, if successful
     */
    abstract public boolean process(HttpServletRequest req, HttpServletResponse resp);

    /**
     * 检查消息是否合法
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @return true, if legal
     */
    abstract public boolean check(HttpServletRequest req, HttpServletResponse resp);

    /**
     * 获得错误消息
     *
     * @return the err msg
     */
    public String getErrMsg() {
        return errMsgBuilder.toString();
    }

    /**
     * 添加错误消息
     *
     * @param errMsg the err msg
     */
    public void addErrMsg(String errMsg) {
        errMsgBuilder.append(errMsg);
    }
}
