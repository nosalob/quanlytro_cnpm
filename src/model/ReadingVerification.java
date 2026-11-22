package model;
import java.io.Serializable;
import java.util.Date;

public class ReadingVerification implements Serializable{
	public static final String STATUS_OPEN = "OPEN";
	public static final String STATUS_CLOSED = "CLOSED";
	
	private int id;
	private String reason;
	private String method;		// content of the verification request
	private String status;
	private String result;		// conclusion of the verification
	private Date requestedAt;
	private Date closedAt;
	
	public ReadingVerification() {
		super();
	}
	public ReadingVerification(String reason, String method) {
		super();
		this.reason = reason;
		this.method = method;
		this.status = STATUS_OPEN;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public Date getRequestedAt() {
		return requestedAt;
	}
	public void setRequestedAt(Date requestedAt) {
		this.requestedAt = requestedAt;
	}
	public Date getClosedAt() {
		return closedAt;
	}
	public void setClosedAt(Date closedAt) {
		this.closedAt = closedAt;
	}
}
