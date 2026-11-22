package model;
import java.io.Serializable;
import java.util.ArrayList;

public class Building implements Serializable{
	private int id;
	private String code;
	private String name;
	private String address;
	private String status;
	private ArrayList<Room> rooms;
	
	public Building() {
		super();
		rooms = new ArrayList<Room>();
	}
	public Building(String code, String name, String address, String status) {
		super();
		this.code = code;
		this.name = name;
		this.address = address;
		this.status = status;
		rooms = new ArrayList<Room>();
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
	public ArrayList<Room> getRooms() {
		return rooms;
	}
	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
}
