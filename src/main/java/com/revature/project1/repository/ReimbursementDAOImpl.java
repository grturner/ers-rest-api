package com.revature.project1.repository;

import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.model.ReimbursementType;
import com.revature.project1.model.User;
import com.revature.project1.utility.ConnectionUtility;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReimbursementDAOImpl implements ReimbursementDAO {
    @Override
    public List<Reimbursement> getByUser(User user) {
        return null;
    }

    @Override
    public List<Reimbursement> getAllPending() {
        return null;
    }

    @Override
    public boolean updateChange(Reimbursement r) {
        Logger.getGlobal().log(Level.INFO, "Entering ReimbursementDAOImpl.updateChange()");
        String sql = "UPDATE ERS_REIMBURSEMENT SET REIMB_AMOUNT = ?, REIMB_SUBMITTED = ?, REIMB_RESOLVED = ?," +
                "REIMB_DESCRIPTION = ?, REIMB_RECEIPT = ?, REIMB_AUTHOR = ?, REIMB_RESOLVER = ?, REIMB_STATUS_ID = ?," +
                "REIMB_TYPE_ID = ? WHERE REIMB_ID = ?";
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setDouble(1, r.getAmount());
            stmt.setTimestamp(2, r.getTimeSubmitted());
            if (r.getTimeResolved() == null && r.getResolver() != null) {
                stmt.setTimestamp(3, getCurrentTimeStamp());
            } else {
                stmt.setTimestamp(3, r.getTimeResolved());
            }
            stmt.setString(4, r.getDescription());
            stmt.setBlob(5, r.getReceipt());
            stmt.setInt(6, r.getSubmitter().getUserId());
            stmt.setInt(7, r.getResolver().getUserId());
            stmt.setInt(8, r.getStatus().getId());
            stmt.setInt(9, r.getType().getId());
            stmt.setInt(10, r.getId());
            if(stmt.executeUpdate() > 0)
                return true;
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.toString(), ex);
        }
        return false;
    }

    @Override
    public List<ReimbursementType> getAllTypes() {
        List<ReimbursementType> typeList = new ArrayList<>();
        String sql = "SELECT * FROM ERS_REIMBURSEMENT_TYPE";
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                ReimbursementType rt = new ReimbursementType();
                rt.setId(rs.getInt("REIMB_TYPE_ID"));
                rt.setType(rs.getString("REIMB_TYPE"));
                typeList.add(rt);
            }
        } catch (SQLException ex) {
            //TODO log4j
            ex.printStackTrace();
        }
        return typeList;
    }

    @Override
    public boolean createReimbursement(Reimbursement reimbursement) {
        String sql = "INSERT INTO ERS_REIMBURSEMENT(REIMB_AMOUNT, REIMB_SUBMITTED, REIMB_DESCRIPTION, REIMB_AUTHOR, " +
                "REIMB_TYPE_ID, REIMB_STATUS_ID) VALUES(?, CURRENT_TIMESTAMP, ?, ?, ?, " +
                "(SELECT REIMB_STATUS_ID FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS = 'Pending'))";
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setDouble(1, reimbursement.getAmount());
            stmt.setString(2, reimbursement.getDescription());
            stmt.setInt(3, reimbursement.getSubmitter().getUserId());
            stmt.setInt(4, reimbursement.getType().getId());
            if(stmt.executeUpdate() > 0) {
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ReimbursementStatus> getAllStatus() {
        String sql = "SELECT * FROM ERS_REIMBURSEMENT_STATUS";
        List<ReimbursementStatus> statusList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                statusList.add(new ReimbursementStatus(rs.getInt("REIMB_STATUS_ID"), rs.getString("REIMB_STATUS")));
            }
        } catch (SQLException ex) {
            //TODO log4j
        }
        return statusList;
    }

    @Override
    public List<Reimbursement> getAllByUserId(int userId) {
        Logger.getGlobal().log(Level.INFO, "Entering ReimbursementDAOImpl.getAllByUserId()");
        String sql = "SELECT * FROM ERS_REIMBURSEMENT WHERE REIMB_AUTHOR = ?";
        List<Reimbursement> reimbList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            reimbList = processResults(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        Logger.getGlobal().log(Level.INFO, "Exiting ReimbursementDAOImpl.getAllByUserId. reimbList was size " + reimbList.size());
        return reimbList;
    }

    @Override
    public List<Reimbursement> getAll() {
        String sql = "SELECT * FROM ERS_REIMBURSEMENT";
        List<Reimbursement> reimbList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            reimbList = processResults(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return reimbList;
    }

    private List<Reimbursement> processResults(ResultSet rs) throws SQLException {
        UserDAOImpl userDAO = new UserDAOImpl();
        List<Reimbursement> reimbList = new ArrayList<>();
        while (rs.next()) {
            Reimbursement r = new Reimbursement();
            r.setId(rs.getInt("REIMB_ID"));
            r.setAmount(rs.getDouble("REIMB_AMOUNT"));
            r.setTimeSubmitted(rs.getTimestamp("REIMB_SUBMITTED"));
            r.setTimeResolved(rs.getTimestamp("REIMB_RESOLVED"));
            r.setDescription(rs.getString("REIMB_DESCRIPTION"));
            r.setReceipt(rs.getBlob("REIMB_RECEIPT"));
            r.setSubmitter(userDAO.getById(rs.getInt("REIMB_AUTHOR")));
            int resolverId = rs.getInt("REIMB_RESOLVER");
            if (rs.wasNull())
                r.setResolver(null);
            else
                r.setResolver(userDAO.getById(resolverId));
            r.setStatus(this.getStatusByID(rs.getInt("REIMB_STATUS_ID")));
            r.setType(this.getTypeByID(rs.getInt("REIMB_TYPE_ID")));
            reimbList.add(r);
        }
        return reimbList;
    }

    private ReimbursementStatus getStatusByID(int id) {
        ReimbursementStatus status = new ReimbursementStatus();
        String sql = "SELECT * FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS_ID = ?";
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                status.setId(rs.getInt("REIMB_STATUS_ID"));
                status.setStatus(rs.getString("REIMB_STATUS"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return status;
    }

    private ReimbursementType getTypeByID(int id) {
        ReimbursementType type = new ReimbursementType();
        String sql = "SELECT * FROM ERS_REIMBURSEMENT_TYPE WHERE REIMB_TYPE_ID = ?";
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                type.setId(rs.getInt("REIMB_TYPE_ID"));
                type.setType(rs.getString("REIMB_TYPE"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return type;
    }

    private static java.sql.Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }

}
