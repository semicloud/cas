package org.semicloud.cas.shared.intensity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.util.Arrays;
import java.util.List;

/**
 * 烈度模型代码过滤器
 */
public class CodeFilter {

    /**
     * 目前支持的模型代码
     */
    private static final String[] SUPPORTED_MODEL_CODES = {"F0100010", "F0100011", "F0100017", "F0100030", "F0100045",
            "F0100046", "F0100047", "M01", "M02", "M03", "M04"};

    /**
     * 过滤，检查目前模型列表是否全部受支持
     *
     * @param list the list 模型列表
     * @return the list 模型列表中受支持的那些
     */
    public static List<String> filte(List<String> list) {
        Predicate contains = new Predicate() {
            @Override
            public boolean evaluate(Object arg0) {
                return Arrays.asList(SUPPORTED_MODEL_CODES).contains((String) arg0);
            }
        };
        // Predicate startsWithM = new Predicate() {
        // @Override
        // public boolean evaluate(Object arg0) {
        // return ((String) arg0).startsWith("M");
        // }
        // };
        CollectionUtils.filter(list, contains);
        return list;
    }
}
