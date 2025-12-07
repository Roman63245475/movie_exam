package easv.my_tunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public class ConnectionManager {

    public static Connection getConnection() throws SQLServerException {
        SQLServerDataSource ds;
        ds = new SQLServerDataSource();
        ds.setDatabaseName("xxx");
        ds.setUser("xxx");
        ds.setPassword("xxx");
        ds.setServerName("xxx");
        ds.setPortNumber('xxx');
        ds.setTrustServerCertificate(true);
        return ds.getConnection();
    }
}