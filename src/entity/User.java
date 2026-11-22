package entity;

import java.util.ArrayList;
import java.util.List;

public class User {
    private int id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String phone;
    private String email;
    private String role;
    private String status;
    private List<PaymentProof> paymentProofs = new ArrayList<>();
    private List<Contract> contracts = new ArrayList<>();

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<PaymentProof> getPaymentProofs() {
        return paymentProofs;
    }

    public void setPaymentProofs(List<PaymentProof> paymentProofs) {
        this.paymentProofs = paymentProofs == null ? new ArrayList<>() : paymentProofs;
    }

    public List<Contract> getContracts() {
        return contracts;
    }

    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts == null ? new ArrayList<>() : contracts;
    }
}
