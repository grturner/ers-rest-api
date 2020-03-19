package com.revature.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.model.ReimbursementType;
import com.revature.project1.repository.ReimbursementDAOImpl;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReimbService {
    private static ReimbService instance = null;
    private List<ReimbursementType> typeList = null;
    private List<ReimbursementStatus> statusList = null;

    private ReimbService() {
        super();
        ReimbursementDAOImpl reimbursementDAO = new ReimbursementDAOImpl();
        typeList = reimbursementDAO.getAllTypes();
        statusList = reimbursementDAO.getAllStatus();
    }

    public static ReimbService getInstance() {
        if (instance == null) {
            instance = new ReimbService();
        }
        return instance;
    }

    public String processGet(String[] uri, Map<String, String[]> params) throws JsonProcessingException {
        String response = null;
        if (uri.length == 1) {
            response = getAll();
        } else if ( uri.length == 2 ) {
            if (uri[1].equals("types")) {
                response = getAllTypes();
            }
            if (uri[1].equals("status")) {
                response = getAllStatus();
            }
            if (uri[1].matches("[0-9]+")) {
                response = getAllByUserId(Integer.parseInt(uri[1]));
            }
        }
        return response;
    }

    public int processPost(String[] uri, Map<String, String[]> params, String json) throws JsonProcessingException {
        int returnCode = -1;
        ObjectMapper mapper = new ObjectMapper();
        ReimbursementDAOImpl reimbursementDAO = new ReimbursementDAOImpl();
        if (uri.length == 1) {
            if (!(json.equals(""))) {
                Reimbursement reimb = mapper.readValue(json, Reimbursement.class);
                reimbursementDAO.createReimbursement(reimb);
                returnCode = 200;
            }
        }
        return returnCode;
    }

    public int processPut(String[] uri, Map<String, String[]> params, String json) throws JsonProcessingException {
        int returnCode = -1;
        ObjectMapper om = new ObjectMapper();
        ReimbursementDAOImpl reimbursementDAO = new ReimbursementDAOImpl();
        if (uri.length == 1) {
            if (!(json.equals(""))) {
                Reimbursement reimb = om.readValue(json, Reimbursement.class);
                if(reimbursementDAO.updateChange(reimb))
                    returnCode = 200;
            }
        }
        return returnCode;
    }

    private String getAllTypes() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(typeList);
    }

    private String getAllByUserId(int i) throws JsonProcessingException {
        Logger.getGlobal().log(Level.INFO, "Entering ReimbService.getAllByUserBytes()");
        ObjectMapper om = new ObjectMapper();
        ReimbursementDAOImpl reimbursementDAO = new ReimbursementDAOImpl();
        return om.writeValueAsString(reimbursementDAO.getAllByUserId(i));
    }

    private String getAll() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        ReimbursementDAOImpl reimbursementDAO = new ReimbursementDAOImpl();
        return om.writeValueAsString(reimbursementDAO.getAll());
    }

    private String getAllStatus() throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.writeValueAsString(statusList);
    }

}
