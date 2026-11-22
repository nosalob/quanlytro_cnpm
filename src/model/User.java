package model;
import java.io.Serializable;
import java.security.MessageDigest;

public class User implements Serializable{
	private int id;
	private String username;
	private String passwordHash;
	private String fullName;
	private String phone;
	private String email;
	private String role;
	private String status;
	
	public User() {
		super();
	}
	public User(String username, String passwordHash, String fullName, String role) {
		super();
		this.username = username;
		this.passwordHash = passwordHash;
		this.fullName = fullName;
		this.role = role;
	}
	
	public static String hash(String plain) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(plain.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for(byte b: bytes)
				sb.append(String.format("%02x", b));
			return sb.toString();
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
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
}
