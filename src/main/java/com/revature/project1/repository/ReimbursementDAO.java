package com.revature.project1.repository;

import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.model.ReimbursementType;
import com.revature.project1.model.User;

import java.util.List;

public interface ReimbursementDAO {

    public List<Reimbursement> getByUser(User user);

    public List<Reimbursement> getAllPending();

    public boolean updateChange(Reimbursement reimbursement);

    public List<ReimbursementType> getAllTypes();

    public void createReimbursement(Reimbursement reimbursement);

    public List<ReimbursementStatus> getAllStatus();

    public List<Reimbursement> getAllByUserId(int userId);

    public List<Reimbursement> getAll();

}
