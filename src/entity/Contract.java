package entity;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contract implements Serializable {
    private int id;
    private String code;
<<<<<<< HEAD
    private float depositAmount;
    private String status = "ACTIVE";
    private LocalDate createdAt;
    private LocalDate terminatedAt;
    private String terminationType;
    private List<ContractTerm> terms = new ArrayList<>();
    private List<Occupancy> occupancies = new ArrayList<>();
=======
    private double depositAmount;
    private String status;
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882

    public Contract() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

<<<<<<< HEAD
    public float getDepositAmount() {
        return depositAmount;
    }
    public void setDepositAmount(float depositAmount) {
        this.depositAmount = depositAmount;
    }
    public void setDepositAmount(double depositAmount) {
        this.depositAmount = (float) depositAmount;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public LocalDate getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDate getTerminatedAt() {
        return terminatedAt;
    }
    public void setTerminatedAt(LocalDate terminatedAt) {
        this.terminatedAt = terminatedAt;
    }
    public String getTerminationType() {
        return terminationType;
    }
    public void setTerminationType(String terminationType) {
        this.terminationType = terminationType;
    }
    public List<ContractTerm> getTerms() {
        return terms;
    }
    public void setTerms(List<ContractTerm> terms) {
        this.terms = terms == null ? new ArrayList<>() : terms;
    }
    public List<Occupancy> getOccupancies() {
        return occupancies;
    }
    public void setOccupancies(List<Occupancy> occupancies) {
        this.occupancies = occupancies == null ? new ArrayList<>() : occupancies;
    }
=======
    public double getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(double depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
}
