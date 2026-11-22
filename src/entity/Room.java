package entity;

<<<<<<< HEAD
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Room implements Serializable {
    private int id;
    private String code;
    private float area;
    private float rentPrice;
    private float depositAmount;
    private int maxOccupants;
    private String status;
    private Building building;
    private List<Contract> contracts = new ArrayList<>();
=======
public class Room {
    private int id;
    private String code;
    private Building building;
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882

    public Room() {
    }

<<<<<<< HEAD
    public Room(String code, float area, String status) {
        this.code = code;
        this.area = area;
        this.status = status;
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
    public float getArea() {
        return area;
    }
    public void setArea(float area) {
        this.area = area;
    }
    public float getRentPrice() {
        return rentPrice;
    }
    public void setRentPrice(float rentPrice) {
        this.rentPrice = rentPrice;
    }
    public float getDepositAmount() {
        return depositAmount;
    }
    public void setDepositAmount(float depositAmount) {
        this.depositAmount = depositAmount;
    }
    public int getMaxOccupants() {
        return maxOccupants;
    }
    public void setMaxOccupants(int maxOccupants) {
        this.maxOccupants = maxOccupants;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Building getBuilding() {
        return building;
    }
    public void setBuilding(Building building) {
        this.building = building;
    }
    public List<Contract> getContracts() {
        return contracts;
    }
    public void setContracts(List<Contract> contracts) {
        this.contracts = contracts;
    }
=======
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
}
