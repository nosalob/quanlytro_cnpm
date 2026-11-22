package entity;

<<<<<<< HEAD
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Building implements Serializable {
=======
public class Building {
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
    private int id;
    private String code;
    private String name;
    private String address;
<<<<<<< HEAD
    private String status;
    private List<Room> rooms = new ArrayList<>();
=======
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882

    public Building() {
    }

<<<<<<< HEAD
    public Building(String code, String name, String address, String status) {
        this.code = code;
        this.name = name;
        this.address = address;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public List<Room> getRooms() {
        return rooms;
    }
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return code + " - " + name;
=======
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    // hiện tên trên combobox
    @Override
    public String toString() {
        return name;
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
    }
}
