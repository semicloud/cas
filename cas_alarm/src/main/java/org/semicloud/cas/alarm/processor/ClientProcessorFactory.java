package org.semicloud.cas.alarm.processor;

/**
 * 消息处理器工厂
 */
public class ClientProcessorFactory {

    /**
     * 根据Client的不同返回不同的处理器对象
     *
     * @param client client的字符串
     * @return the processor
     */
    public static ClientProcessor getProcessor(String client) {
        ClientProcessor processor = null;
        switch (client) {
            case "on-duty":
                processor = new AlarmClientProcessor();
                break;
            case "speclist-edit":
                processor = new EditClientProcessor();
                break;
        }
        return processor;
    }
}
