package com.revature.project1.model;

import java.io.Serializable;

public class ReimbursementStatus implements Serializable {
    private static final long serialVersionUID = -7728089643754592148L;
    int id;
    String status;

    public ReimbursementStatus() {
        super();
    }

    public ReimbursementStatus(int id) {
        super();
        this.id = id;
    }

    public ReimbursementStatus(int id, String status) {
        this(id);
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
