package entity;

import java.sql.Date;

public class Compensation {
    private int id;
    private double amount;
    private String reason;
    private String method;
    private String status;
    private Date createdAt;
    private Date settledAt;

    public Compensation() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getSettledAt() { return settledAt; }
    public void setSettledAt(Date settledAt) { this.settledAt = settledAt; }
}
