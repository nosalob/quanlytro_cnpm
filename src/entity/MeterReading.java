package entity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class MeterReading implements Serializable{
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_REJECTED = "REJECTED";
    public static final String STATUS_VERIFYING = "VERIFYING";

    private int id;
    private int month;
    private int year;
    private String type;				// ELECTRIC or WATER
    private double previousValue;
    private double currentValue;
    private String evidenceImageUrl;
    private Double eInvoiceValue;		// null when the reading has no e-invoice
    private String status;
    private String reviewNote;
    private Date submittedAt;
    private Date reviewedAt;
    private Room room;
    private User submittedBy;
    private User reviewedBy;
    private ArrayList<ReadingVerification> verifications;

    public MeterReading() {
        super();
        verifications = new ArrayList<ReadingVerification>();
    }

    /**
    * consumption is a derived value: it is not stored in the DB
    */
    public double getConsumption() {
        return currentValue - previousValue;
    }

    /**
    * deviation between the current value and the value on the e-invoice
    * (assumption: deviation = currentValue - eInvoiceValue; returns 0 if there is no e-invoice)
    */
    public double calcDeviation() {
        if(eInvoiceValue == null)
        return 0;
        return currentValue - eInvoiceValue;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getMonth() {
        return month;
    }
    public void setMonth(int month) {
        this.month = month;
    }
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public double getPreviousValue() {
        return previousValue;
    }
    public void setPreviousValue(double previousValue) {
        this.previousValue = previousValue;
    }
    public double getCurrentValue() {
        return currentValue;
    }
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }
    public String getEvidenceImageUrl() {
        return evidenceImageUrl;
    }
    public void setEvidenceImageUrl(String evidenceImageUrl) {
        this.evidenceImageUrl = evidenceImageUrl;
    }
    public Double getEInvoiceValue() {
        return eInvoiceValue;
    }
    public void setEInvoiceValue(Double eInvoiceValue) {
        this.eInvoiceValue = eInvoiceValue;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getReviewNote() {
        return reviewNote;
    }
    public void setReviewNote(String reviewNote) {
        this.reviewNote = reviewNote;
    }
    public Date getSubmittedAt() {
        return submittedAt;
    }
    public void setSubmittedAt(Date submittedAt) {
        this.submittedAt = submittedAt;
    }
    public Date getReviewedAt() {
        return reviewedAt;
    }
    public void setReviewedAt(Date reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    public Room getRoom() {
        return room;
    }
    public void setRoom(Room room) {
        this.room = room;
    }
    public User getSubmittedBy() {
        return submittedBy;
    }
    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }
    public User getReviewedBy() {
        return reviewedBy;
    }
    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
    }
    public ArrayList<ReadingVerification> getVerifications() {
        return verifications;
    }
    public void setVerifications(ArrayList<ReadingVerification> verifications) {
        this.verifications = verifications;
    }
}
