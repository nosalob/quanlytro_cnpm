package dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import model.MeterReading;
import model.Room;
import model.User;

public class MeterReadingDAO extends DAO{
	
	public MeterReadingDAO() {
		super();
	}
	
	/**
	 * search all meter readings of the rooms in the building @buildingId, in the @month/@year, whose status is @status
	 * @return list of meter readings (each reading contains its room)
	 */
	public ArrayList<MeterReading> searchMeterReading(int buildingId, int month, int year, String status){
		ArrayList<MeterReading> result = new ArrayList<MeterReading>();
		String sql = "SELECT m.id, m.month, m.year, m.type, m.previousValue, m.currentValue, m.status, "
				+ "r.id AS roomId, r.code AS roomCode, r.area AS roomArea, r.status AS roomStatus "
				+ "FROM tblMeterReading m JOIN tblRoom r ON r.id = m.tblRoomid "
				+ "WHERE r.tblBuildingid = ? AND m.month = ? AND m.year = ? AND m.status = ? "
				+ "ORDER BY r.code, m.type";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, buildingId);
			ps.setInt(2, month);
			ps.setInt(3, year);
			ps.setString(4, status);
			ResultSet rs = ps.executeQuery();

			while(rs.next()){
				MeterReading mr = new MeterReading();
				mr.setId(rs.getInt("id"));
				mr.setMonth(rs.getInt("month"));
				mr.setYear(rs.getInt("year"));
				mr.setType(rs.getString("type"));
				mr.setPreviousValue(rs.getDouble("previousValue"));
				mr.setCurrentValue(rs.getDouble("currentValue"));
				mr.setStatus(rs.getString("status"));
				
				Room room = new Room();
				room.setId(rs.getInt("roomId"));
				room.setCode(rs.getString("roomCode"));
				room.setArea(rs.getFloat("roomArea"));
				room.setStatus(rs.getString("roomStatus"));
				mr.setRoom(room);
				result.add(mr);
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		return result;
	}
	
	/**
	 * get the full information of the meter reading whose id is @readingId
	 * (its room, evidence image, e-invoice value, submitter, reviewer...)
	 * @return the meter reading, or null if it does not exist
	 */
	public MeterReading getReadingDetail(int readingId) {
		MeterReading mr = null;
		String sql = "SELECT m.*, r.id AS roomId, r.code AS roomCode, r.area AS roomArea, r.status AS roomStatus, "
				+ "s.id AS submitterId, s.fullName AS submitterName, v.id AS reviewerId, v.fullName AS reviewerName "
				+ "FROM tblMeterReading m JOIN tblRoom r ON r.id = m.tblRoomid "
				+ "LEFT JOIN tblUser s ON s.id = m.tblResidentid "
				+ "LEFT JOIN tblUser v ON v.id = m.tblUserid "
				+ "WHERE m.id = ?";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, readingId);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
				mr = new MeterReading();
				mr.setId(rs.getInt("id"));
				mr.setMonth(rs.getInt("month"));
				mr.setYear(rs.getInt("year"));
				mr.setType(rs.getString("type"));
				mr.setPreviousValue(rs.getDouble("previousValue"));
				mr.setCurrentValue(rs.getDouble("currentValue"));
				mr.setEvidenceImageUrl(rs.getString("evidenceImageUrl"));
				double ev = rs.getDouble("eInvoiceValue");
				if(!rs.wasNull())
					mr.setEInvoiceValue(ev);
				mr.setStatus(rs.getString("status"));
				mr.setReviewNote(rs.getString("reviewNote"));
				mr.setSubmittedAt(rs.getDate("submittedAt"));
				mr.setReviewedAt(rs.getDate("reviewedAt"));
				
				Room room = new Room();
				room.setId(rs.getInt("roomId"));
				room.setCode(rs.getString("roomCode"));
				room.setArea(rs.getFloat("roomArea"));
				room.setStatus(rs.getString("roomStatus"));
				mr.setRoom(room);
				
				if(rs.getInt("submitterId") > 0) {
					User submitter = new User();
					submitter.setId(rs.getInt("submitterId"));
					submitter.setFullName(rs.getString("submitterName"));
					mr.setSubmittedBy(submitter);
				}
				if(rs.getInt("reviewerId") > 0) {
					User reviewer = new User();
					reviewer.setId(rs.getInt("reviewerId"));
					reviewer.setFullName(rs.getString("reviewerName"));
					mr.setReviewedBy(reviewer);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}	
		return mr;
	}
	
	/**
	 * approve the reading: set its status to APPROVED and record the reviewer and the review date
	 */
	public boolean approve(int readingId, int reviewerId) {
		return review(readingId, reviewerId, MeterReading.STATUS_APPROVED, null);
	}
	
	/**
	 * reject the reading: set its status to REJECTED and record the reviewer, the review date and the @reason
	 */
	public boolean reject(int readingId, int reviewerId, String reason) {
		return review(readingId, reviewerId, MeterReading.STATUS_REJECTED, reason);
	}
	
	/**
	 * update the status, then the reviewer in a single transaction
	 */
	private boolean review(int readingId, int reviewerId, String status, String note) {
		String sqlReview = "UPDATE tblMeterReading SET tblUserid = ?, reviewedAt = ?"
				+ (note != null ? ", reviewNote = ?" : "") + " WHERE id = ?";
		boolean result = true;
		try {
			con.setAutoCommit(false);
			if(!updateStatus(readingId, status))
				throw new Exception("cannot update the status of the reading " + readingId);
			
			PreparedStatement ps = con.prepareStatement(sqlReview);
			int i = 1;
			ps.setInt(i++, reviewerId);
			ps.setDate(i++, new java.sql.Date(System.currentTimeMillis()));
			if(note != null)
				ps.setString(i++, note);
			ps.setInt(i, readingId);
			ps.executeUpdate();
			con.commit();
		}catch(Exception e) {
			result = false;
			e.printStackTrace();
			try {
				con.rollback();
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}finally {
			try {
				con.setAutoCommit(true);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	/**
	 * update the status of the reading
	 */
	public boolean updateStatus(int readingId, String status) {
		String sql = "UPDATE tblMeterReading SET status = ? WHERE id = ?";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, status);
			ps.setInt(2, readingId);
			ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}		
		return true;
	}
	
	/**
	 * update the current value of the reading with the actual value found by the verification
	 */
	public boolean updateValue(int readingId, double actualValue) {
		String sql = "UPDATE tblMeterReading SET currentValue = ? WHERE id = ?";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDouble(1, actualValue);
			ps.setInt(2, readingId);
			ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}		
		return true;
	}
}
