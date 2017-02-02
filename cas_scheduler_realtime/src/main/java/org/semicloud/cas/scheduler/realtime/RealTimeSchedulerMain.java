package org.semicloud.cas.scheduler.realtime;

import com.supermap.data.GeoRegion;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.semicloud.cas.shared.EditRegion;
import org.semicloud.cas.shared.EditResult;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * 重计算调度器入口函数
 */
public class RealTimeSchedulerMain {

    /**
     * 调度器实例对象
     */
    private static RealTimeScheduler _scheduler = RealTimeScheduler.getInstance();

    /**
     * The main method.
     *
     * @param args the arguments
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException        Signals that an I/O exception has occurred.
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        Thread thread = new Thread(_scheduler);
        thread.start();
    }

    /**
     * 获得人工编辑结果
     *
     * @return the edits the result 人工编辑结果对象
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException        Signals that an I/O exception has occurred.
     */
    public static EditResult getEditResult() throws URISyntaxException, IOException {
        JSONObject jsonObject = getTestData();

        EditResult editResult = new EditResult();
        editResult.setEqID(jsonObject.getString("eqID"));
        editResult.setTaskID(jsonObject.getString("taskID"));

        JSONArray circles = jsonObject.getJSONArray("circles");
        for (int i = 0; i < circles.size(); i++) {
            JSONObject circle = circles.getJSONObject(i);
            EditRegion editRegion = new EditRegion();
            editRegion.setLongitude(circle.getDouble("longitude"));
            editRegion.setLatitude(circle.getDouble("latitude"));
            editRegion.setIntensity((float) circle.getDouble("intensity"));
            JSONArray points = circle.getJSONArray("points");
            Point2Ds point2Ds = new Point2Ds();
            for (int j = 0; j < points.size(); j++) {
                JSONObject point = points.getJSONObject(j);
                Point2D point2d = new Point2D(point.getDouble("x"), point.getDouble("y"));
                point2Ds.add(point2d);
            }

            GeoRegion region = new GeoRegion(point2Ds);
            editRegion.setGeoRegion(region);

            editResult.addRegion(editRegion);
        }
        return editResult;
    }

    /**
     * 从文本数据获取研判结果，测试用，现在已不用
     *
     * @return the test data
     * @throws URISyntaxException the URI syntax exception
     * @throws IOException        Signals that an I/O exception has occurred.
     */
    @Deprecated
    public static JSONObject getTestData() throws URISyntaxException, IOException {
        StringBuilder sb = new StringBuilder();
        File file = new File(ClassLoader.getSystemResource("data.txt").toURI());
        List<String> allLines = Files.readAllLines(file.toPath(), Charset.defaultCharset());
        for (String line : allLines) {
            sb.append(line);
        }
        return JSONObject.fromObject(sb.toString());
    }
}
