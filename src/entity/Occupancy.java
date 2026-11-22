package entity;

import java.io.Serializable;
import java.time.LocalDate;

public class Occupancy implements Serializable {
    private int id;
    private int isRepresentative;
    private LocalDate moveInDate;
    private LocalDate moveOutDate;
    private Resident resident;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getIsRepresentative() {
        return isRepresentative;
    }
    public void setIsRepresentative(int isRepresentative) {
        this.isRepresentative = isRepresentative;
    }
    public boolean isRepresentative() {
        return isRepresentative == 1;
    }
    public void setRepresentative(boolean rep) {
        this.isRepresentative = rep ? 1 : 0;
    }
    public LocalDate getMoveInDate() {
        return moveInDate;
    }
    public void setMoveInDate(LocalDate moveInDate) {
        this.moveInDate = moveInDate;
    }
    public LocalDate getMoveOutDate() {
        return moveOutDate;
    }
    public void setMoveOutDate(LocalDate moveOutDate) {
        this.moveOutDate = moveOutDate;
    }
    public Resident getResident() {
        return resident;
    }
    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
