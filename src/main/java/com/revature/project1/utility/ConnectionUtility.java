package com.revature.project1.utility;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionUtility {
    private static ConnectionUtility instance = null;
    private static DataSource dataSource = null;

    private ConnectionUtility() {
        try {
            /**
             *  Had issues with dataSource remaining null if tomcat was started before
             *  Oracle-DB had started.
             */
            while (dataSource == null ) {
                Context initialContext = new InitialContext();
                Context environmentContext = (Context) initialContext.lookup("java:comp/env");
                String dataResourceName = "jdbc/Project1";
                dataSource = (DataSource) environmentContext.lookup(dataResourceName);
            }
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
