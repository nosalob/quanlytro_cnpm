package entity;

import java.io.Serializable;
import java.time.LocalDate;

public class ContractTerm implements Serializable {
    private int id;
    private int termNo;
    private LocalDate startDate;
    private LocalDate endDate;
    private float rentPrice;
    private String status;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getTermNo() {
        return termNo;
    }
    public void setTermNo(int termNo) {
        this.termNo = termNo;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public float getRentPrice() {
        return rentPrice;
    }
    public void setRentPrice(float rentPrice) {
        this.rentPrice = rentPrice;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
