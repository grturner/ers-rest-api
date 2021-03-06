package com.revature.project1.repository;

import com.revature.project1.model.User;
import com.revature.project1.utility.ConnectionUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    Logger logger = LogManager.getLogger(UserDAOImpl.class);

    @Override
    public User getUser(String userName) {
        String sql = "SELECT * FROM ERS_USERS JOIN ERS_USER_ROLES EUR on ERS_USERS.USER_ROLE_ID = EUR.ERS_USER_ROLE " +
                "WHERE ERS_USERNAME = ?";
        List<User> userList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery()) {
                    userList = parseUsers(rs);
                }
            }
        } catch (SQLException ex) {
            logger.debug("Exception: getUser()", ex);
        }
        if (userList.size() == 1)
            return userList.get(0);
        else
            return null;
    }

    @Override
    public User getById(int id) {
        String sql = "SELECT * FROM ERS_USERS JOIN ERS_USER_ROLES EUR on ERS_USERS.USER_ROLE_ID = EUR.ERS_USER_ROLE " +
                "WHERE ERS_USERS_ID = ?";
        List<User> userList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    userList = parseUsers(rs);
                }
            }
        } catch (SQLException ex) {
            logger.debug("Exception: getById()", ex);
        }
        if (userList.size() == 1) {
            return userList.get(0);
        }
        return null;
    }

    @Override
    public boolean verifyPassword(String userName, String password) {
        String sql = "SELECT ERS_PASSWORD FROM ERS_USERS WHERE ERS_USERNAME = ?";
        int counter = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, userName);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        if (password.equals(rs.getString("ERS_PASSWORD")))
                            counter++;
                    }
                }
            }
        } catch (SQLException ex) {
            logger.debug("Exception: verifyPassword()", ex);
        }
        return counter > 0;
    }

    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO ERS_USERS(ERS_USERNAME, ERS_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_ROLE_ID) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionUtility.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, user.getUserName());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getFirstName());
                stmt.setString(4, user.getLastName());
                stmt.setString(5, user.getEmail());
                stmt.setInt(6, 1);
                int res = stmt.executeUpdate();
                if (res > 0) {
                    String msg = "User has registered: ".concat(user.getUserName());
                    logger.info(msg);
                    return true;
                }
            }
        } catch (SQLException ex) {
            logger.debug("Exception: createUser()", ex);
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM ERS_USERS JOIN ERS_USER_ROLES EUR on ERS_USERS.USER_ROLE_ID = EUR.ERS_USER_ROLE";
        List<User> userList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            try (Statement stmt = con.createStatement()) {
                try (ResultSet rs = stmt.executeQuery(sql)) {
                    userList = parseUsers(rs);
                }
            }
        } catch (SQLException ex) {
            logger.debug("Exception: getAll()", ex);
        }
        return userList;
    }

    private List<User> parseUsers(ResultSet rs) throws SQLException {
        List<User> tempList = new ArrayList<>();
        while(rs.next()) {
            tempList.add(new User(
                    rs.getInt("ERS_USERS_ID"),
                    rs.getString("ERS_USERNAME"),
                    rs.getString("ERS_PASSWORD"),
                    rs.getString("USER_FIRST_NAME"),
                    rs.getString("USER_LAST_NAME"),
                    rs.getString("USER_EMAIL"),
                    rs.getString("USER_ROLE")
            ));
        }
        return tempList;
    }
}
