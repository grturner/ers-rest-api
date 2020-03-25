package com.revature.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.model.ReimbursementType;
import com.revature.project1.model.User;
import com.revature.project1.repository.ReimbursementDAOImpl;
import com.revature.project1.repository.UserDAOImpl;
import com.revature.project1.utility.ConnectionUtility;
import hthurow.tomcatjndi.TomcatJNDI;
import netscape.javascript.JSException;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ReimbServiceTest {
    private static ReimbService service = new ReimbService();

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

    @Test
    public void constructorTest() {
        ReimbService s = new ReimbService(new ReimbursementDAOImpl());
        assertNotNull(s);
    }

    @Test
    public void processGetAllTest() {
        String[] uri = { "reimbursements" };
        Map<String, String[]> params = null;
        List<Reimbursement> reimbList = null;
        int count = 0;
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT";
        try (Connection con = ConnectionUtility.getConnection()) {
            String json = service.processGet(uri, params);
            ObjectMapper mapper = new ObjectMapper();
            reimbList = mapper.readValue(json, new TypeReference<List<Reimbursement>>() {
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
        assertEquals(count, reimbList.size());
    }

    @Test
    public void processGetTypesTest() {
        String[] uri = { "reimbursements", "types" };
        Map<String, String[]> params = null;
        List<ReimbursementType> typeList = null;
        try {
            String json = service.processGet(uri, params);
            ObjectMapper mapper = new ObjectMapper();
            typeList = mapper.readValue(json, new TypeReference<List<ReimbursementType>>(){});
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        assert(typeList.size() == 4);
    }

    @Test
    public void processGetStatusTest() {
        String[] uri = { "reimbursements", "status" };
        Map<String, String[]> params = null;
        List<ReimbursementStatus> statusList = null;
        try {
            String json = service.processGet(uri, params);
            ObjectMapper mapper = new ObjectMapper();
            statusList = mapper.readValue(json, new TypeReference<List<ReimbursementStatus>>() {
            });
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        assert(statusList.size() == 3);
    }

    @Test
    public void processGetByUserId() {
        String[] uri = { "reimbursements", "141" };
        Map<String, String[]> params = null;
        List<Reimbursement> reimbList = null;
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_AUTHOR = 141";
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()) {
            String json = service.processGet(uri, params);
            ObjectMapper mapper = new ObjectMapper();
            reimbList = mapper.readValue(json, new TypeReference<List<Reimbursement>>() {
            });
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (JsonProcessingException | SQLException ex) {
            ex.printStackTrace();
        }
        assertEquals(count, reimbList.size());
    }

    @Test
    public void processPostTest() {
        int initCount = 0;
        int finalCount = 0;
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_AUTHOR = 141";
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                initCount = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            String jsonBuilder = service.processGet(new String[]{"", "141"}, null);
            ObjectMapper mapper = new ObjectMapper();
            List<Reimbursement> reimbList = mapper.readValue(jsonBuilder, new TypeReference<List<Reimbursement>>() {
            });
            Reimbursement r = reimbList.get(0);
            String jsonPayload = mapper.writeValueAsString(r);
            service.processPost(new String[]{""}, null, jsonPayload);
            stmt = con.createStatement();
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                finalCount = rs.getInt(1);
            }
        } catch (JsonProcessingException | SQLException ex) {
            ex.printStackTrace();
        }
        assert(finalCount == initCount + 1);
    }

    @Test
    public void processPut() {
        String[] uri = {"reimbursements"};
        UserDAOImpl userDAO = new UserDAOImpl();
        ReimbursementDAOImpl dao = new ReimbursementDAOImpl();
        List<Reimbursement> pendingList = dao.getAllPending();
        List<ReimbursementStatus> statusList = dao.getAllStatus();
        ReimbursementStatus denied = null;
        for (ReimbursementStatus s : statusList) {
            if (s.getStatus().equals("Denied"))
                denied = s;
        }
        User u = userDAO.getById(141);
        Reimbursement r = pendingList.get(0);
        r.setStatus(denied);
        r.setResolver(u);
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(r);
            assert(service.processPut(uri, null, json) == 200);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void processGetPending() {
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_STATUS_ID = " +
                "(SELECT REIMB_STATUS_ID FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS = 'Pending')";
        String[] uri = {"reimbursements", "pending"};
        List<Reimbursement> reimbList = null;
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()){
            String jsonResponse = service.processGet(uri, null);
            ObjectMapper mapper = new ObjectMapper();
            reimbList = mapper.readValue(jsonResponse, new TypeReference<List<Reimbursement>>() {
            });
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            assertEquals(count, reimbList.size());
        } catch (SQLException | JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void processGetApproved() {
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_STATUS_ID = " +
                "(SELECT REIMB_STATUS_ID FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS = 'Approved')";
        String[] uri = {"reimbursements", "approved"};
        List<Reimbursement> reimbList = null;
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()){
            String jsonResponse = service.processGet(uri, null);
            ObjectMapper mapper = new ObjectMapper();
            reimbList = mapper.readValue(jsonResponse, new TypeReference<List<Reimbursement>>() {
            });
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            assertEquals(count, reimbList.size());
        } catch (SQLException | JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void processGetDenied() {
        String sql = "SELECT COUNT(REIMB_ID) FROM ERS_REIMBURSEMENT WHERE REIMB_STATUS_ID = " +
                "(SELECT REIMB_STATUS_ID FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS = 'Denied')";
        String[] uri = {"reimbursements", "denied"};
        List<Reimbursement> reimbList = null;
        int count = 0;
        try (Connection con = ConnectionUtility.getConnection()){
            String jsonResponse = service.processGet(uri, null);
            ObjectMapper mapper = new ObjectMapper();
            reimbList = mapper.readValue(jsonResponse, new TypeReference<List<Reimbursement>>() {
            });
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                count = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            assertEquals(count, reimbList.size());
        } catch (SQLException | JsonProcessingException ex) {
            ex.printStackTrace();
        }
    }
}