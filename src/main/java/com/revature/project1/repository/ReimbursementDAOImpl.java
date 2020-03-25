package com.revature.project1.repository;

import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.model.ReimbursementType;
import com.revature.project1.utility.ConnectionUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReimbursementDAOImpl implements ReimbursementDAO {
    Logger logger = LogManager.getLogger(ReimbursementDAOImpl.class);

    @Override
    public List<Reimbursement> getAllPending() {
        List<Reimbursement> pendingList = new ArrayList<>();
        String sql = "SELECT * FROM ERS_REIMBURSEMENT WHERE REIMB_STATUS_ID = " +
                "(SELECT REIMB_STATUS_ID FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS = 'Pending')";
        try (Connection con = ConnectionUtility.getConnection()) {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            pendingList = processResults(rs);
            rs.close();
            stmt.close();
        } catch (SQLException | IOException ex) {
            logger.debug("Exception: getAllPending()", ex);
        }
        return pendingList;
    }

    @Override
    public boolean updateChange(Reimbursement r) {
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
            if (r.getReceipt() != null) {
                Blob blob = con.createBlob();
                blob.setBytes(1, r.getReceipt().getBytes());
                stmt.setBlob(5, blob);
            } else {
                stmt.setNull(5, Types.BLOB);
            }
            stmt.setInt(6, r.getSubmitter().getUserId());
            stmt.setInt(7, r.getResolver().getUserId());
            stmt.setInt(8, r.getStatus().getId());
            stmt.setInt(9, r.getType().getId());
            stmt.setInt(10, r.getId());
            int result = stmt.executeUpdate();
            stmt.close();
            logger.info("Updated Reimbursement with ID: ".concat(String.valueOf(r.getId())));
            if (result > 0)
                return true;
        } catch (SQLException ex) {
            logger.debug("Exception: updateChange()", ex);
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
            stmt.close();
            rs.close();
        } catch (SQLException ex) {
            logger.debug("Exception: getAllTypes()", ex);
        }
        return typeList;
    }

    @Override
    public boolean createReimbursement(Reimbursement reimbursement) {
        String sql = "INSERT INTO ERS_REIMBURSEMENT(REIMB_AMOUNT, REIMB_SUBMITTED, REIMB_DESCRIPTION, REIMB_AUTHOR, " +
                "REIMB_TYPE_ID, REIMB_STATUS_ID, REIMB_RECEIPT) VALUES(?, CURRENT_TIMESTAMP, ?, ?, ?, " +
                "(SELECT REIMB_STATUS_ID FROM ERS_REIMBURSEMENT_STATUS WHERE REIMB_STATUS = 'Pending'), ?)";
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setDouble(1, reimbursement.getAmount());
            stmt.setString(2, reimbursement.getDescription());
            stmt.setInt(3, reimbursement.getSubmitter().getUserId());
            stmt.setInt(4, reimbursement.getType().getId());
            if (reimbursement.getReceipt() != null) {
                Blob blob = con.createBlob();
                blob.setBytes(1, reimbursement.getReceipt().getBytes());
                stmt.setBlob(5, blob);
            } else {
                stmt.setNull(5, Types.BLOB);
            }
            int result = stmt.executeUpdate();
            stmt.close();
            logger.info("New reimbursement ticket created.");
            if (result > 0)
                return true;
        } catch (SQLException ex) {
            logger.debug("Exception: createReimbursement()", ex);
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
            stmt.close();
            rs.close();
        } catch (SQLException ex) {
            logger.debug("Exception: getAllStatus", ex);
        }
        return statusList;
    }

    @Override
    public List<Reimbursement> getAllByUserId(int userId) {
        String sql = "SELECT * FROM ERS_REIMBURSEMENT WHERE REIMB_AUTHOR = ?";
        List<Reimbursement> reimbList = new ArrayList<>();
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            reimbList = processResults(rs);
            stmt.close();
            rs.close();
        } catch (SQLException | IOException ex) {
            logger.debug("Exception: getAllByUserId()", ex);
        }
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
            stmt.close();
            rs.close();
        } catch (SQLException | IOException ex) {
            logger.debug("Exception: getAll()", ex);
        }
        return reimbList;
    }

    @Override
    public List<Reimbursement> getByStatus(ReimbursementStatus status) {
        UserDAOImpl userDao = new UserDAOImpl();
        List<Reimbursement> reimbList = new ArrayList<>();
        String sql = "SELECT * FROM ERS_REIMBURSEMENT WHERE REIMB_STATUS_ID = ?";
        try (Connection con = ConnectionUtility.getConnection()) {
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, status.getId());
            ResultSet rs =  stmt.executeQuery();
            reimbList = processResults(rs);
            rs.close();
            stmt.close();
        } catch (SQLException | IOException ex) {
            logger.debug("Exception: getByStatus()", ex);
        }
        return reimbList;
    }

    private List<Reimbursement> processResults(ResultSet rs) throws SQLException, IOException {
        UserDAOImpl userDAO = new UserDAOImpl();
        List<Reimbursement> reimbList = new ArrayList<>();
        while (rs.next()) {
            Reimbursement r = new Reimbursement();
            r.setId(rs.getInt("REIMB_ID"));
            r.setAmount(rs.getDouble("REIMB_AMOUNT"));
            r.setTimeSubmitted(rs.getTimestamp("REIMB_SUBMITTED"));
            r.setTimeResolved(rs.getTimestamp("REIMB_RESOLVED"));
            r.setDescription(rs.getString("REIMB_DESCRIPTION"));
            Blob blob = rs.getBlob("REIMB_RECEIPT");
            if (!rs.wasNull()) {
                StringBuilder str = new StringBuilder();
                String buffer;
                BufferedReader br = new BufferedReader(new InputStreamReader(blob.getBinaryStream()));
                while((buffer=br.readLine()) != null) {
                    str.append(buffer);
                }
                br.close();
                r.setReceipt(str.toString());
            }
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
            stmt.close();
            rs.close();
        } catch (SQLException ex) {
            logger.debug("Exception: getStatusById()", ex);
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
            stmt.close();
            rs.close();
        } catch (SQLException ex) {
            logger.debug("Exception: getTypeById:()", ex);
        }
        return type;
    }

    private static java.sql.Timestamp getCurrentTimeStamp() {
        java.util.Date today = new java.util.Date();
        return new java.sql.Timestamp(today.getTime());
    }

}
