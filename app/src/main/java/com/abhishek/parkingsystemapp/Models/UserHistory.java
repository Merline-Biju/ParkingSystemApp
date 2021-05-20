package com.abhishek.parkingsystemapp.Models;

import com.google.firebase.Timestamp;

public class UserHistory {

    String transactionId;
    Timestamp arrival;
    Timestamp exit;
    double amount;

    public UserHistory() {
    }

    public UserHistory(String transactionId, Timestamp arrival, Timestamp exit, double amount) {
        this.transactionId = transactionId;
        this.arrival = arrival;
        this.exit = exit;
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Timestamp getArrival() {
        return arrival;
    }

    public void setArrival(Timestamp arrival) {
        this.arrival = arrival;
    }

    public Timestamp getExit() {
        return exit;
    }

    public void setExit(Timestamp exit) {
        this.exit = exit;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
