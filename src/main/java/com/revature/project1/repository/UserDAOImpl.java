package com.revature.project1.repository;

import com.revature.project1.model.User;
import com.revature.project1.utility.ConnectionUtility;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {
    @Override
    public User getUser(String userName) {
        String sql = "SELECT * FROM ERS_USERS JOIN ERS_USER_ROLES EUR on ERS_USERS.USER_ROLE_ID = EUR.ERS_USER_ROLE " +
                "WHERE ERS_USERNAME = ?";
        List<User> userList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            userList = parseUsers(rs);
        } catch (SQLException ex) {
            //TODO logging
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
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            userList = parseUsers(rs);
        } catch (SQLException ex) {
            //TODO logging
        }
        if (userList.size() == 1) {
            return userList.get(0);
        }
        return null;
    }

    @Override
    public boolean checkUsername(String userName) {
        return false;
    }

    @Override
    public boolean updateUser(User user) {
        return false;
    }

    @Override
    public boolean createUser(User user) {
        String sql = "INSERT INTO ERS_USERS(ERS_USERNAME, ERS_PASSWORD, USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL, USER_ROLE_ID) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, user.getUserName());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setInt(6, 1);
            if (stmt.executeUpdate() > 0)
                return true;
        } catch (SQLException ex) {
            //TODO logging
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        String sql = "SELECT * FROM ERS_USERS JOIN ERS_USER_ROLES EUR on ERS_USERS.USER_ROLE_ID = EUR.ERS_USER_ROLE";
        List<User> userList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            userList = parseUsers(rs);
        } catch (SQLException ex) {
            //TODO logging
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
