package com.abhishek.parkingsystemapp.Models;

import com.google.firebase.Timestamp;

public class ParkingSlot {

    boolean booked;
    String userId;
    String slotId;
    String transactionId;

    public ParkingSlot() {
    }

    public ParkingSlot(boolean booked, String userId, String slotId, String transactionId) {
        this.booked = booked;
        this.userId = userId;
        this.slotId = slotId;
        this.transactionId = transactionId;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
}
