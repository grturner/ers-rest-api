package com.revature.project1.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.project1.model.Reimbursement;
import com.revature.project1.model.ReimbursementStatus;
import com.revature.project1.repository.ReimbursementDAO;
import com.revature.project1.repository.ReimbursementDAOImpl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            if (uri[1].toLowerCase().equals("pending")) {
                ReimbursementStatus pStatsus = null;
                for (ReimbursementStatus s : repository.getAllStatus()) {
                    if (s.getStatus().equals("Pending"))
                        pStatsus = s;
                }
                response = getByStatus(pStatsus);
            }
            if (uri[1].toLowerCase().equals("approved")) {
                ReimbursementStatus aStatus = null;
                for (ReimbursementStatus s : repository.getAllStatus()) {
                    if (s.getStatus().equals("Approved"))
                        aStatus = s;
                }
                response = getByStatus(aStatus);
            }
            if (uri[1].toLowerCase().equals("denied")) {
                ReimbursementStatus dStatsus = null;
                for (ReimbursementStatus s : repository.getAllStatus()) {
                    if (s.getStatus().equals("Denied"))
                        dStatsus = s;
                }
                response = getByStatus(dStatsus);
            }
        }
        return response;
    }

    public int processPost(String[] uri, Map<String, String[]> params, String json) throws JsonProcessingException {
        int returnCode = -1;
        if (uri.length == 1) {
            if (!(json.equals(""))) {
                Logger.getGlobal().log(Level.INFO, "ReimbService.processPost(): creating a reimbursement with: ".concat(json));
                Reimbursement reimb = mapper.readValue(json, Reimbursement.class);
                 if (repository.createReimbursement(reimb)) {
                     returnCode = 200;
                 }
            }
        }
        return returnCode;
    }

    public int processPut(String[] uri, Map<String, String[]> params, String json) throws JsonProcessingException {
        int returnCode = -1;
        if (uri.length == 1) {
            if (!(json.equals(""))) {
                Reimbursement reimb = mapper.readValue(json, Reimbursement.class);
                if(repository.updateChange(reimb))
                    returnCode = 200;
            }
        }
        return returnCode;
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
