package org.semicloud.cas.model.usv;

import com.supermap.data.*;

/**
 * 该类用于绘制线源模型的影响场
 * Created by Administrator on 2017/2/6.
 */
@Deprecated
public class INTENSITY2_USV extends USVBase {
    public static void main(String[] args) {
        double L = 200 * 1000, R = 100 * 1000;
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

        DatasetVectorInfo datasetVectorInfo = getDatasetVectorInfo(rectangleDataset, DatasetType.REGION);
        if (datasource.getDatasets().get(rectangleDataset) != null) {
            datasource.getDatasets().delete(rectangleDataset);
            System.out.println("Dataset Test deleted!");
        }
        DatasetVector datasetVector = datasource.getDatasets().create(datasetVectorInfo);
        datasetVector.getFieldInfos().add(getFieldInfo("L", FieldType.DOUBLE));
        datasetVector.getFieldInfos().add(getFieldInfo("R", FieldType.DOUBLE));
//        datasetVector.setPrjCoordSys(prjCoordSys);
        Point2D center = getExProjection(longitude, latitude);
        GeoRectangle rectangle = new GeoRectangle(center, L, 2 * R, rotate);
        Point2Ds rectanglePoints = rectangle.convertToRegion().getPart(0);
        for (int i = 0; i < rectanglePoints.getCount(); i++) {
            Point2D point2D = rectanglePoints.getItem(i);
            System.out.println("point " + (i + 1) + point2D.toString());
        }

        Point2D pieCenter1 = new Point2D();
        pieCenter1.setX((rectanglePoints.getItem(0).getX() + rectanglePoints.getItem(1).getX()) / 2);
        pieCenter1.setY((rectanglePoints.getItem(0).getY() + rectanglePoints.getItem(1).getY()) / 2);
        GeoPie pie1 = new GeoPie(pieCenter1, R, R, 0, 180, rotate + 90);

        Point2D pieCenter2 = new Point2D();
        pieCenter2.setX((rectanglePoints.getItem(2).getX() + rectanglePoints.getItem(3).getX()) / 2);
        pieCenter2.setY((rectanglePoints.getItem(2).getY() + rectanglePoints.getItem(3).getY()) / 2);
        GeoPie pie2 = new GeoPie(pieCenter2, R, R, 0, 180, rotate + 270);

        Recordset recordset = datasetVector.getRecordset(false, CursorType.DYNAMIC);
        recordset.addNew(rectangle.convertToRegion());
        recordset.setDouble("L", L);
        recordset.setDouble("R", R);
        recordset.update();

        recordset.addNew(pie1.convertToRegion(72));
        recordset.setDouble("L", L);
        recordset.setDouble("R", R);
        recordset.update();

        recordset.addNew(pie2.convertToRegion(72));
        recordset.setDouble("L", L);
        recordset.setDouble("R", R);
        recordset.update();

        recordset.close();
        recordset.dispose();
        datasetVector.close();

        //
        GeoRegion rectangleRegion = rectangle.convertToRegion();
        allPoints.add(rectangleRegion.getPart(0).getItem(0)); // 加入矩形的第一个点

        GeoRegion pieRegion1 = pie1.convertToRegion(72);
        for (int i = 0; i < pieRegion1.getPart(0).getCount(); i++) {
            allPoints.add(pieRegion1.getPart(0).getItem(i));
        }

        allPoints.add(rectangleRegion.getPart(0).getItem(1));
        allPoints.add(rectangleRegion.getPart(0).getItem(2));

        GeoRegion pieRegion2 = pie2.convertToRegion(72);
        for (int i = 0; i < pieRegion2.getPart(0).getCount(); i++) {
            allPoints.add(pieRegion2.getPart(0).getItem(i));
        }

        allPoints.add(rectangleRegion.getPart(0).getItem(3));
        allPoints.add(rectangleRegion.getPart(0).getItem(4)); // 加入矩形的第一个点，封闭面对象

        DatasetVectorInfo datasetVectorInfo1 = new DatasetVectorInfo("FinalResult", DatasetType.REGION);
        if (datasource.getDatasets().get("FinalResult") != null) {
            datasource.getDatasets().delete("FinalResult");
            System.out.println("delete dataset FinalResult");
        }
        DatasetVector datasetVector1 = datasource.getDatasets().create(datasetVectorInfo1);
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
