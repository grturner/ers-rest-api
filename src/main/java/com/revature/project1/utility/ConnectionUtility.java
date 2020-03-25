package com.revature.project1.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionUtility {
    private static DataSource dataSource = null;
    private static Logger logger = LogManager.getLogger(ConnectionUtility.class);

    private ConnectionUtility() {
        super();
    }

    public static Connection getConnection() throws SQLException {
        try {
            while (dataSource == null ) {
                Context initialContext = new InitialContext();
                Context environmentContext = (Context) initialContext.lookup("java:comp/env");
                String dataResourceName = "jdbc/Project1";
                dataSource = (DataSource) environmentContext.lookup(dataResourceName);
            }
            return dataSource.getConnection();
        } catch (NamingException ex) {
            logger.error(ex);
        }
        return null;
    }

    public static void setDataSource(DataSource dataSource) {
        ConnectionUtility.dataSource = dataSource;
    }

    public static boolean isActive() {
        return dataSource != null;
    }
}
