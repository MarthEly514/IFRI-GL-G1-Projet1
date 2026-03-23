package com.campusdocs.server.dto.request;

public class RejectDemandeRequest {
    private String rejectReason;

    public RejectDemandeRequest() {}

    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}