package entity;

public class Room {
    private int id;
    private String code;
    private Building building;

    public Room() {
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Building getBuilding() { return building; }
    public void setBuilding(Building building) { this.building = building; }
}
