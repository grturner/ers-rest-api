package com.revature.project1.model;

import java.io.Serializable;

public class ReimbursementType implements Serializable {
    private static final long serialVersionUID = 2329811661146633734L;
    private int id;
    private String type;

    public ReimbursementType() {
        super();
    }

    public ReimbursementType(int id) {
        super();
        this.id = id;
    }

    public ReimbursementType(int id, String type) {
        this(id);
        this.type = type;
    }

    public ReimbursementType(String type) {
        super();
        this.id = 0;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
