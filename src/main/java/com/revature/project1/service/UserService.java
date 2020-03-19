package com.revature.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project1.model.User;
import com.revature.project1.repository.UserDAO;
import com.revature.project1.repository.UserDAOImpl;
import java.util.Map;

public class UserService {
    private UserDAO repository;
    private ObjectMapper mapper;

    public UserService() {
        super();
        this.repository = new UserDAOImpl();
        this.mapper = new ObjectMapper();
    }

    public UserService(UserDAO repository) {
        super();
        this.repository = repository;
        this.mapper = new ObjectMapper();
    }


    public String processGet(String[] uri, Map<String, String[]> params) throws JsonProcessingException {
        String response = null;
        if (uri.length == 1) {
            response = getAll();
        } else {
            if (uri[1].matches("[0-9]+"))
                response = getById(Integer.parseInt(uri[1]));
            else {
                if (params.containsKey("login")) {
                    response = verifyLogin(uri[1], params.get("login")[0]);
                } else {
                    response = getByUsername(uri[1]);
                }
            }

        }
        return response;
    }

    public int processPost(String[] uri, Map<String, String[]> params, String json) throws JsonProcessingException {
        int returnCode = -1;
        if (uri.length == 1) {
            if (!((json.equals("")))){
                User u = mapper.readValue(json, User.class);
                if(repository.createUser(u))
                    returnCode = 200;
            }
        }
        return returnCode;
    }

    private String getAll() throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getAll());
    }

    private String getById(int id) throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getById(id));
    }

    private String getByUsername(String name) throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getUser(name));
    }

    private String verifyLogin(String userName, String password) throws JsonProcessingException {
        return mapper.writeValueAsString(repository.verifyPassword(userName, password));
    }
}
