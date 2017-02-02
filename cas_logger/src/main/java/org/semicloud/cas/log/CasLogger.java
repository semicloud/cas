package org.semicloud.cas.log;

import org.semicloud.utils.db.BaseDao;
import org.semicloud.utils.db.factory.DaoFactory;

import java.sql.Timestamp;

public class CasLogger {
    public static void writeLogToDB(String eqID, Timestamp time, LogLevel level, Class class1, String msg) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String className = class1.getCanonicalName();
        String sql = "INSERT INTO APP_LOG VALUES (?,?,?,?,?)";
        BaseDao odao = DaoFactory.getInstance();
        odao.update(sql, eqID, timestamp, level.toString(), className, msg);
    }

    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        writeLogToDB("S12820E08642020150309110454", getCurrentTime(), LogLevel.INFO, CasLogger.class, "this");
    }
}
