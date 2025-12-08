package easv.my_tunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.Connection;

public class ConnectionManager {

    public static Connection getConnection() throws SQLServerException {
        SQLServerDataSource ds;
        ds = new SQLServerDataSource();
        ds.setDatabaseName("my_tunes_db"); // make this unique as names are shared on server
        ds.setUser("xxx"); // Use your own username
        ds.setPassword("xxx"); // Use your own password
        ds.setServerName("10.176.111.34");
        ds.setPortNumber(1433);
        ds.setTrustServerCertificate(true);
        return ds.getConnection();
    }
}