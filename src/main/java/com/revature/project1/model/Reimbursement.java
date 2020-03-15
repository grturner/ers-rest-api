package com.revature.project1.model;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Timestamp;

public class Reimbursement implements Serializable {
    private int id;
    private double amount;
    private Timestamp timeSubmitted;
    private Timestamp timeResolved;
    private String description;
    private Blob receipt;
    private User submitter;
    private User resolver;
    private String status;
    private String type;
}

