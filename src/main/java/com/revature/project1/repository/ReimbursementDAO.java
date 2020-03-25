package com.revature.project1.repository;

import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.model.ReimbursementType;

import java.util.List;

public interface ReimbursementDAO {

    public List<Reimbursement> getAllPending();

    public boolean updateChange(Reimbursement reimbursement);

    public List<ReimbursementType> getAllTypes();

    public boolean createReimbursement(Reimbursement reimbursement);

    public List<ReimbursementStatus> getAllStatus();

    public List<Reimbursement> getAllByUserId(int userId);

    public List<Reimbursement> getAll();

    public List<Reimbursement> getByStatus(ReimbursementStatus status);

}
