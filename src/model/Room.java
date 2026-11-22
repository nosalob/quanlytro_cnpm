package model;
import java.io.Serializable;

public class Room implements Serializable{
	private int id;
	private String code;
	private float area;
	private String status;
	
	public Room() {
		super();
	}
	public Room(String code, float area, String status) {
		super();
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
