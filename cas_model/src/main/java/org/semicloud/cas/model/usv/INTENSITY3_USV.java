package org.semicloud.cas.model.usv;

import com.supermap.data.*;

/**
 * 该类用于绘制线源模型的影响场
 * Created by Administrator on 2017/2/6.
 */
public class INTENSITY3_USV extends USVBase {
    public static void main(String[] args) {
        double L = 200 * 1000;
        double R = 100 * 1000;
        double rotate = 60;
        double longitude = 103.1, latitude = 33.2;

        Point2Ds allPoints = new Point2Ds();

        String rectangleDataset = "Rectangle";
        String circleDataset = "Circle";

        Workspace ws = new Workspace();
        DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
        datasourceConnectionInfo.setServer("ORCL");
        datasourceConnectionInfo.setUser("MAPTEST");
        datasourceConnectionInfo.setPassword("maptest");
        datasourceConnectionInfo.setEngineType(EngineType.ORACLEPLUS);
        datasourceConnectionInfo.setDatabase("");
        datasourceConnectionInfo.setAlias("ForABetterLife");
        Datasource datasource = ws.getDatasources().open(datasourceConnectionInfo);
        if (datasource.isOpened()) {
            System.out.println("datasource is open.");
        }
        PrjCoordSys prjCoordSys = new PrjCoordSys();
        prjCoordSys.fromFile("c:\\ExProjectionStandard.prj", PrjFileType.ESRI);

        DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(rectangleDataset, DatasetType.LINE);
        if (datasource.getDatasets().get(rectangleDataset) != null) {
            datasource.getDatasets().delete(rectangleDataset);
            System.out.println("Dataset Test deleted!");
        }
        DatasetVector datasetVector = datasource.getDatasets().create(datasetVectorInfo);
        datasetVector.getFieldInfos().add(getFieldInfo("L", FieldType.DOUBLE));
        datasetVector.getFieldInfos().add(getFieldInfo("R", FieldType.DOUBLE));
        datasetVector.setPrjCoordSys(prjCoordSys);
        Point2D center = getExProjection(longitude, latitude);

        GeoRectangle rectangle = new GeoRectangle(center, L, 2 * R, rotate);
        Point2Ds rectanglePoints = rectangle.convertToRegion().getPart(0);
        for (int i = 0; i < rectanglePoints.getCount(); i++) {
            Point2D point2D = rectanglePoints.getItem(i);
            System.out.println("point " + (i + 1) + point2D.toString());
        }

        Point2D arcCenter1 = new Point2D();
        arcCenter1.setX((rectanglePoints.getItem(0).getX() + rectanglePoints.getItem(1).getX()) / 2);
        arcCenter1.setY((rectanglePoints.getItem(0).getY() + rectanglePoints.getItem(1).getY()) / 2);
        GeoArc arc1 = new GeoArc(arcCenter1, R, 0, 180);
        arc1.rotate(arcCenter1, rotate + 90);

        Point2D arcCenter2 = new Point2D();
        arcCenter2.setX((rectanglePoints.getItem(2).getX() + rectanglePoints.getItem(3).getX()) / 2);
        arcCenter2.setY((rectanglePoints.getItem(2).getY() + rectanglePoints.getItem(3).getY()) / 2);
        GeoArc arc2 = new GeoArc(arcCenter2, R, 0, 180);
        arc2.rotate(arcCenter2, rotate + 270);

        Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
        recordset.addNew(rectangle.convertToLine());
        recordset.setDouble("L", L);
        recordset.setDouble("R", R);
        recordset.update();

        recordset.addNew(arc1.convertToLine(72));
        recordset.setDouble("L", L);
        recordset.setDouble("R", R);
        recordset.update();

        recordset.addNew(arc2.convertToLine(72));
        recordset.setDouble("L", L);
        recordset.setDouble("R", R);
        recordset.update();

        recordset.close();
        recordset.dispose();
        datasetVector.close();

        // 加入矩形的第一个点，即左上角的点，逆时针1,2,3,4个点，1，2点对应arc1,3，4点对应arc2
        allPoints.add(rectanglePoints.getItem(0));

        Point2Ds arc1Points = arc1.convertToLine(72).getPart(0);
        for (int i = 0; i < arc1Points.getCount(); i++) {
            allPoints.add(arc1Points.getItem(i));
        }

        allPoints.add(rectanglePoints.getItem(1));
        allPoints.add(rectanglePoints.getItem(2));

        Point2Ds arc2Points = arc2.convertToLine(72).getPart(0);
        for (int i = 0; i < arc2Points.getCount(); i++) {
            allPoints.add(arc2Points.getItem(i));
        }

        allPoints.add(rectanglePoints.getItem(3));
        allPoints.add(rectanglePoints.getItem(4)); // 加入矩形的第一个点，封闭面对象

        DatasetVectorInfo datasetVectorInfo1 = new DatasetVectorInfo("FinalResult", DatasetType.REGION);
        if (datasource.getDatasets().get("FinalResult") != null) {
            datasource.getDatasets().delete("FinalResult");
            System.out.println("delete dataset FinalResult");
        }
        DatasetVector datasetVector1 = datasource.getDatasets().create(datasetVectorInfo1);
        datasetVector1.setPrjCoordSys(prjCoordSys);
        System.out.println("create dataset FinalResult");
        Recordset finalRecordSet = datasetVector1.getRecordset(false, CursorType.DYNAMIC);
        finalRecordSet.addNew(new GeoRegion(allPoints));
        finalRecordSet.update();
        finalRecordSet.close();
        finalRecordSet.dispose();
        datasetVector1.close();

        ws.getDatasources().closeAll();
        System.out.println("datasource closed..");
    }


}
