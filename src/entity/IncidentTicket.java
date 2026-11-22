package entity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class IncidentTicket {
    private int id;
    private String code;
    private String title;
    private String description;
    private String imageUrl;
    private String status;
    private String faultType;
    private String resolutionNote;
    private Date createdAt;
    private Date resolvedAt;
    private Room room;
    private User user; // tài khoản của người thuê tạo ticket
    private List<Compensation> compensations = new ArrayList<>();

    public IncidentTicket() {
    }

<<<<<<< HEAD
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
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getFaultType() {
        return faultType;
    }
    public void setFaultType(String faultType) {
        this.faultType = faultType;
    }
    public String getResolutionNote() {
        return resolutionNote;
    }
    public void setResolutionNote(String resolutionNote) {
        this.resolutionNote = resolutionNote;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getResolvedAt() {
        return resolvedAt;
    }
    public void setResolvedAt(Date resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public List<Compensation> getCompensations() {
        return compensations;
    }
=======
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFaultType() { return faultType; }
    public void setFaultType(String faultType) { this.faultType = faultType; }
    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Date resolvedAt) { this.resolvedAt = resolvedAt; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Compensation> getCompensations() { return compensations; }
>>>>>>> 6f87f1b33f69b96e935fd7a7829142150423a882
    public void setCompensations(List<Compensation> compensations) {
        this.compensations = compensations == null ? new ArrayList<>() : compensations;
    }
}
