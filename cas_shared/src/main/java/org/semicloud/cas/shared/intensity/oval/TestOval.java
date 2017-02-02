package org.semicloud.cas.shared.intensity.oval;

import org.semicloud.cas.shared.utils.SharedCpt;
import org.semicloud.utils.common.Convert;

/**
 * 烈度模型的测试类，确定烈度模型的输出是否合法
 *
 * @author LiQinyong
 */
public class TestOval {
    private static final String[] SUPPORTED_MODEL_CODES = {"F0100010", "F0100011", "F0100017", "F0100030", "F0100045",
            "F0100046", "F0100047", "M01", "M02", "M03", "M04"};

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        System.out.println("烈度模型计算结果一览：");
        for (String code : SUPPORTED_MODEL_CODES) {
            Oval oval = loadModel(code);
            System.out.println("================CODE:" + code + "==================");
            for (float m = 5.5f; m <= 9.0f; m += 1.0f) {
                // TODO 2015年9月22日，下面的代码是旧的计算中心烈度的方法，需要的时候可以恢复
                float i_m = SharedCpt.getEpiIntensity(m);
                // 下面是新的（王海鹰提供的）方法
                // float i_m = Epi.getValue(m, depth);
                System.out.println("  magnitude:" + m);
                for (float i = 6.0f; i <= i_m; i += 1.0f) {
                    double longAxis = Convert.toDouble(String.valueOf(oval.calcLongAxis(i, m, 10.0f)), "#0.00");
                    double shortAxis = Convert.toDouble(String.valueOf(oval.calcShortAxis(i, m, 10.0f)), ".3f");
                    if (longAxis < 0 || shortAxis < 0) {
                        System.err.println("\t" + "i=" + i + ", LongAxis=" + longAxis + ", ShortAxis=" + shortAxis);
                    } else {
                        System.out.println("\t" + "i=" + i + ", LongAxis=" + longAxis + ", ShortAxis=" + shortAxis);
                    }
                }
            }
        }
    }

    private static Oval loadModel(String code) {
        try {
            Class<?> c = Class.forName("org.semicloud.cas.shared.intensity.oval.".concat(code));
            return (Oval) c.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        System.err.println("ERROR!");
        return new F0100010();
    }
}
