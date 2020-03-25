package com.revature.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project1.model.Login;
import com.revature.project1.model.User;
import com.revature.project1.repository.UserDAOImpl;
import com.revature.project1.utility.ConnectionUtility;
import hthurow.tomcatjndi.TomcatJNDI;
import org.junit.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class UserServiceTest {
    private UserService service = new UserService();

    @BeforeClass
    public static void setupBeforeClass() {
        if (!ConnectionUtility.isActive()) {
            TomcatJNDI tomcatJNDI = new TomcatJNDI();
            tomcatJNDI.processContextXml(new File("/home/grturner/workspace/Project1/target/test-classes/context.xml"));
            tomcatJNDI.start();
            try {
                DataSource ds = (DataSource) InitialContext.doLookup("java:comp/env/jdbc/Project1");
                ConnectionUtility.setDataSource(ds);
            } catch (NamingException ex) {
                ex.printStackTrace();
                fail("The gibson ain't working today");
            }
        }
    }

    @AfterClass
    public static void tearDownAfterClass() {
        String sql = "DELETE FROM ERS_USERS WHERE ERS_USERNAME ='bobross'";
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void constructorTest() {
        UserService us = new UserService(new UserDAOImpl());
        assertNotNull(us);
    }

    @Test
    public void processGetAll() {
        String sql = "SELECT COUNT(ERS_USERS_ID) FROM ERS_USERS";
        int count = 0;
        String[] uri = {"users"};
        List<User> userList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            String json = service.processGet(uri, null);
            ObjectMapper mapper = new ObjectMapper();
            userList = mapper.readValue(json, new TypeReference<List<User>>() {
            });
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (JsonProcessingException | SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(count, userList.size());
    }

    @Test
    public void processGetByUserName() {
        String[] uri = {"users", "grturner"};
        Map<String, String[]> params = new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = service.processGet(uri, params);
            User u = mapper.readValue(json, User.class);
            assert(u.getUserId() == 141);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void processGetById() {
        String[] uri = {"users", "141"};
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = service.processGet(uri, null);
            User u = mapper.readValue(json, User.class);
            assert(u.getUserName().equals("grturner"));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void processPutLogin() {
//        String[] uri = {"users", "jsmith"};
//        Map<String, String[]> params = new HashMap<>();
//        String[] param = {"crazypassword"};
//        params.put("login", param);
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            String json = service.processGet(uri, params);
//            Boolean bool = mapper.readValue(json, Boolean.class);
//            assert(bool);
//        } catch (JsonProcessingException ex) {
//            ex.printStackTrace();
//        }
        String[] uri = {"users", "jsmith"};
        String login = "{\"username\":\"jsmith\",\"password\":\"password\"}";
        Map<String, String[]> params = new HashMap<>();
        try {
            assert (service.processPost(uri, params, login).equals("true"));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void processPostCreateUser() {
        String[] uri = {"users"};
        User u = new User();
        u.setUserName("bobross");
        u.setLastName("Ross");
        u.setFirstName("Bob");
        u.setEmail("bobross@bobross.com");
        u.setPassword("bobross");
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(u);
            assert(service.processPost(uri, null, json).equals("200"));
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}