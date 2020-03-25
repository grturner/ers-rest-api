package com.revature.project1.repository;

import com.revature.project1.model.User;
import com.revature.project1.utility.ConnectionUtility;
import com.revature.project1.utility.PasswordUtility;
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
import java.util.List;

import static org.junit.Assert.*;

public class UserDAOImplTest {
    UserDAOImpl dao = new UserDAOImpl();

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
        String sql = "DELETE FROM ERS_USERS WHERE ERS_USERNAME = 'jtester'";
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
    public void getUserTest() {
        User user = dao.getUser("grturner");
        String sql = "SELECT * FROM ERS_USERS WHERE ERS_USERNAME = 'grturner'";
        int userId = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                userId = rs.getInt("ERS_USERS_ID");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(userId, user.getUserId());
    }

    @Test
    public void getByIdTest() {
        User user = dao.getById(141);
        String userName = "";
        String sql = "SELECT * FROM ERS_USERS WHERE ERS_USERS_ID = 141";
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                userName = rs.getString("ERS_USERNAME");
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assert(userName.equals(user.getUserName()));
    }

    @Test
    public void verifyPasswordTest() {
        assert(dao.verifyPassword("jsmith", PasswordUtility.sha512Hash("password")));
    }

    @Test
    public void createUserTest() {
        User u = new User();
        u.setEmail("test@test.test");
        u.setFirstName("JUnit");
        u.setLastName("Tester");
        u.setUserName("jtester");
        u.setPassword("junitrocks");
        dao.createUser(u);
        int count = 0;
        String sql = "SELECT * FROM ERS_USERS WHERE ERS_USERNAME = 'jtester'";
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count++;
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assert(count == 1);
    }

    @Test
    public void getAllTest() {
        List<User> userList = dao.getAll();
        String sql = "SELECT COUNT(ERS_USERS_ID) FROM ERS_USERS";
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(count, userList.size());
    }
}