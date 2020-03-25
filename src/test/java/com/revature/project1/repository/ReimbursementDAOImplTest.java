package com.revature.project1.repository;

import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.model.ReimbursementType;
import com.revature.project1.model.User;
import com.revature.project1.utility.ConnectionUtility;
import hthurow.tomcatjndi.TomcatJNDI;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.sql.*;
import java.util.List;

import static org.junit.Assert.*;

public class ReimbursementDAOImplTest {
    public static ReimbursementDAOImpl dao = new ReimbursementDAOImpl();

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

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getAllStatusTest() {
        List<ReimbursementStatus> statusList = dao.getAllStatus();
        String sql = "SELECT COUNT(REIMB_STATUS_ID) FROM ERS_REIMBURSEMENT_STATUS";
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(count, statusList.size());
    }

    @Test
    public void getAllTypesTest() {
        List<ReimbursementType> typeList = dao.getAllTypes();
        String sql = "SELECT COUNT(REIMB_TYPE_ID) FROM ERS_REIMBURSEMENT_TYPE";
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(count, typeList.size());
    }

    @Test
    public void getAllTest() {
        List<Reimbursement> reimbList = dao.getAll();
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT";
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(count, reimbList.size());
    }

    @Test
    public void getAllByUserIdTest() {
        List<Reimbursement> reimbList = dao.getAllByUserId(141);
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_AUTHOR = 141";
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
        assertEquals(count, reimbList.size());
    }

    @Test
    public void createReimbursementTest() {
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_AUTHOR = 141";
        int countBefore = 0;
        int countAfter = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                countBefore = rs.getInt(1);
            }
            Reimbursement r = new Reimbursement();
            rs.close();
            stmt.close();
            r.setAmount(Math.random() * 500);
            r.setType(dao.getAllTypes().get(0));
            r.setDescription("TestCase: createReimbursementTest()");
            User u = new User();
            u.setUserId(141);
            r.setSubmitter(u);
            dao.createReimbursement(r);
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                countAfter = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assertTrue(countAfter == countBefore + 1);
    }

    @Test
    public void getAllPendingTest() {
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_STATUS_ID = " +
                "(SELECT REIMB_STATUS_ID FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS = 'Pending')";
        List<Reimbursement> pendingList = dao.getAllPending();
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(count, pendingList.size());
    }

    @Test
    public void updateChangeTest() {
        List<Reimbursement> pendingList = dao.getAllPending();
        String sql = "SELECT * FROM ERS_REIMBURSEMENT WHERE REIMB_ID = ?";
        Reimbursement r = null;
        boolean result = false;
        if (pendingList.size() == 0)
            fail("Populate the database with pending tickets");
        else {
            r = pendingList.get(0);
            User u = new User();
            u.setUserId(141);
            r.setResolver(u);
            List<ReimbursementStatus> statusList = dao.getAllStatus();
            ReimbursementStatus status = null;
            for (ReimbursementStatus s : statusList) {
                if (s.getStatus().equals("Approved")){
                    status = s;
                    break;
                }
            }
            r.setStatus(status);
            dao.updateChange(r);
            try (Connection con = ConnectionUtility.getConnection()) {
                PreparedStatement stmt = con.prepareStatement(sql);
                stmt.setInt(1, r.getId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("REIMB_STATUS_ID") == status.getId())
                        result = true;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        assert(result);


    }
}