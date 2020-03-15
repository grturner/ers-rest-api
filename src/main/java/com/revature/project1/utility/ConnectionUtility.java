package com.revature.project1.utility;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtility {
    private static ConnectionUtility instance = null;
    private static DataSource dataSource = null;

    private ConnectionUtility() {
        try {
            Context initialContext = new InitialContext();
            Context environmentContext = (Context) initialContext.lookup("java:comp/env");
            String dataResourceName = "jdbc/Project1";
            dataSource = (DataSource) environmentContext.lookup(dataResourceName);
        } catch (NamingException ex) {
            //TODO logger
        }
    }

    public static Connection getConnection() throws SQLException {
        if (instance == null)
            instance = new ConnectionUtility();
        return dataSource.getConnection();
    }
}
