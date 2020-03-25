package com.revature.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.repository.ReimbursementDAO;
import com.revature.project1.repository.ReimbursementDAOImpl;

public class ReimbService {
    private ReimbursementDAO repository;
    private ObjectMapper mapper;

    public ReimbService() {
        super();
        this.repository = new ReimbursementDAOImpl();
        this.mapper = new ObjectMapper();
    }

    public ReimbService(ReimbursementDAO repository) {
        super();
        this.repository = repository;
        this.mapper = new ObjectMapper();
    }

    public String processGet(String[] uri) throws JsonProcessingException {
        String response = null;
        if (uri.length == 1) {
            response = getAll();
        } else if ( uri.length == 2 ) {
            response = routeGetLength2(uri);
        }
        return response;
    }

    public int processPost(String[] uri, String json) throws JsonProcessingException {
        int returnCode = -1;
        if (uri.length == 1 && !(json.equals(""))) {
            Reimbursement reimb = mapper.readValue(json, Reimbursement.class);
             if (repository.createReimbursement(reimb)) {
                 returnCode = 200;
             }
        }
        return returnCode;
    }

    public int processPut(String[] uri, String json) throws JsonProcessingException {
        int returnCode = -1;
        if (uri.length == 1 && !(json.equals(""))) {
            Reimbursement reimb = mapper.readValue(json, Reimbursement.class);
            if(repository.updateChange(reimb))
                returnCode = 200;
        }
        return returnCode;
    }

    private String routeGetLength2(String[] uri) throws JsonProcessingException {
        String response = null;
        switch (uri[1]) {
            case "types":
                response = getAllTypes();
                break;
            case "status":
                response = getAllStatus();
                break;
            case "pending":
                response = getAllPending();
                break;
            case "denied":
                response = getAllDenied();
                break;
            case "approved":
                response = getAllApproved();
                break;
            default:
                response = getAllByUserId(Integer.parseInt(uri[1]));
        }
        return response;
    }

    private String getAllApproved() throws JsonProcessingException {
        ReimbursementStatus aStatus = null;
        for (ReimbursementStatus s : repository.getAllStatus()) {
            if (s.getStatus().equals("Approved"))
                aStatus = s;
        }
        return getByStatus(aStatus);
    }

    private String getAllPending() throws JsonProcessingException {
        ReimbursementStatus pStatsus = null;
        for (ReimbursementStatus s : repository.getAllStatus()) {
            if (s.getStatus().equals("Pending"))
                pStatsus = s;
        }
        return getByStatus(pStatsus);
    }

    private String getAllDenied() throws JsonProcessingException {
        ReimbursementStatus dStatsus = null;
        for (ReimbursementStatus s : repository.getAllStatus()) {
            if (s.getStatus().equals("Denied"))
                dStatsus = s;
        }
        return getByStatus(dStatsus);
    }

    private String getAllTypes() throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getAllTypes());
    }

    private String getAllStatus() throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getAllStatus());
    }

    private String getAllByUserId(int i) throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getAllByUserId(i));
    }

    private String getAll() throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getAll());
    }

    private String getByStatus(ReimbursementStatus status) throws JsonProcessingException {
        return mapper.writeValueAsString(repository.getByStatus(status));
    }
}
