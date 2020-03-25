package com.revature.project1.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Reimbursement implements Serializable {
    private static final long serialVersionUID = 5854017514368957171L;
    private int id;
    private double amount;
    private Timestamp timeSubmitted;
    private Timestamp timeResolved;
    private String description;
    private String receipt;
    private User submitter;
    private User resolver;
    private transient ReimbursementStatus status;
    private transient ReimbursementType type;

    public Reimbursement() {
        super();
    }

    public Reimbursement(double amount, String description, User submitter, ReimbursementType type) {
        this.amount = amount;
        this.description = description;
        this.submitter = submitter;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(Timestamp timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public Timestamp getTimeResolved() {
        return timeResolved;
    }

    public void setTimeResolved(Timestamp timeResolved) {
        this.timeResolved = timeResolved;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public User getResolver() {
        return resolver;
    }

    public void setResolver(User resolver) {
        this.resolver = resolver;
    }

    public ReimbursementStatus getStatus() {
        return status;
    }

    public void setStatus(ReimbursementStatus status) {
        this.status = status;
    }

    public ReimbursementType getType() {
        return type;
    }

    public void setType(ReimbursementType type) {
        this.type = type;
    }
}

