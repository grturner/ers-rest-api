package com.revature.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project1.model.User;
import com.revature.project1.repository.UserDAOImpl;

import java.util.List;

public class UserService {
    private static UserService instance = null;

    private UserService() {
        super();
    }

    public static UserService getInstance() {
        if (instance == null)
            instance = new UserService();
        return instance;
    }

    public String processGet(String[] uri) throws JsonProcessingException {
        String response = null;
        if (uri.length == 1) {
            response = getAll();
        } else {
            if (uri[1].matches("[0-9]+"))
                response = getById(Integer.parseInt(uri[1]));
            else
                response = getByUsername(uri[1]);
        }
        return response;
    }

    private String getAll() throws JsonProcessingException {
        UserDAOImpl userDAO = new UserDAOImpl();
        List<User> userList = userDAO.getAll();
        return new ObjectMapper().writeValueAsString(userList);
    }

    private String getById(int id) throws JsonProcessingException {
        UserDAOImpl userDAO = new UserDAOImpl();
        return new ObjectMapper().writeValueAsString(userDAO.getById(id));
    }

    private String getByUsername(String name) throws JsonProcessingException {
        UserDAOImpl userDAO = new UserDAOImpl();
        return new ObjectMapper().writeValueAsString(userDAO.getUser(name));
    }
}
