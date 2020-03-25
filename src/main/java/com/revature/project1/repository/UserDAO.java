package com.revature.project1.repository;

import com.revature.project1.model.User;

import java.util.List;

public interface UserDAO {

    public User getUser(String userName);

    public User getById(int id);

    public boolean verifyPassword(String userName, String password);

    public boolean createUser(User user);

    public List<User> getAll();
}
