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
    private ReimbursementStatus status;
    private ReimbursementType type;

    public Reimbursement() {
        super();
    }

    public Reimbursement(int id) {
        super();
        this.id = id;
    }

    public Reimbursement(int id, double amount) {
        this(id);
        this.amount = amount;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted) {
        this(id, amount);
        this.timeSubmitted = timeSubmitted;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted, Timestamp timeResolved) {
        this(id, amount, timeSubmitted);
        this.timeResolved = timeResolved;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted, Timestamp timeResolved, String description) {
        this(id, amount, timeSubmitted, timeResolved);
        this.description = description;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted, Timestamp timeResolved, String description,
                         Blob receipt){
        this(id, amount, timeSubmitted, timeResolved, description);
        this.receipt = receipt;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted, Timestamp timeResolved, String description,
                         Blob receipt, User submitter) {
        this(id, amount, timeSubmitted, timeResolved, description, receipt);
        this.submitter = submitter;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted, Timestamp timeResolved, String description,
                         Blob receipt, User submitter, User resolver) {
        this(id, amount, timeSubmitted, timeResolved, description, receipt, submitter);
        this.resolver = resolver;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted, Timestamp timeResolved, String description,
                              Blob receipt, User submitter, User resolver, ReimbursementStatus status) {
        this(id, amount, timeSubmitted, timeResolved, description, receipt, submitter, resolver);
        this.status = status;
    }

    public Reimbursement(int id, double amount, Timestamp timeSubmitted, Timestamp timeResolved, String description,
                         Blob receipt, User submitter, User resolver, ReimbursementStatus status, ReimbursementType type) {
        this(id, amount, timeSubmitted, timeResolved, description, receipt, submitter, resolver, status);
        this.type = type;
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

    public Blob getReceipt() {
        return receipt;
    }

    public void setReceipt(Blob receipt) {
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

