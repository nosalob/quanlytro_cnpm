package dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import model.ReadingVerification;

public class ReadingVerificationDAO extends DAO{
	
	public ReadingVerificationDAO() {
		super();
	}
	
	/**
	 * add a new verification request for the reading @readingId
	 * the id of the new verification is set into @v
	 */
	public boolean addVerification(int readingId, ReadingVerification v) {
		String sql = "INSERT INTO tblReadingVerification(reason, method, status, requestedAt, tblMeterReadingid) VALUES(?,?,?,?,?)";
		try{
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, v.getReason());
			ps.setString(2, v.getMethod());
			ps.setString(3, ReadingVerification.STATUS_OPEN);
			ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
			ps.setInt(5, readingId);
			ps.executeUpdate();
			
			//get id of the new inserted verification
			ResultSet generatedKeys = ps.getGeneratedKeys();
			if (generatedKeys.next()) {
				v.setId(generatedKeys.getInt(1));
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * get the open verification of the reading @readingId
	 * @return the verification, or null if there is not any
	 */
	public ReadingVerification getVerificationByReading(int readingId) {
		ReadingVerification v = null;
		String sql = "SELECT * FROM tblReadingVerification WHERE tblMeterReadingid = ? AND status = ? ORDER BY id DESC LIMIT 1";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, readingId);
			ps.setString(2, ReadingVerification.STATUS_OPEN);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()){
				v = new ReadingVerification();
				v.setId(rs.getInt("id"));
				v.setReason(rs.getString("reason"));
				v.setMethod(rs.getString("method"));
				v.setStatus(rs.getString("status"));
				v.setResult(rs.getString("result"));
				v.setRequestedAt(rs.getDate("requestedAt"));
				v.setClosedAt(rs.getDate("closedAt"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return v;
	}
	
	/**
	 * close the verification @v with its result (conclusion)
	 */
	public boolean resolveVerification(ReadingVerification v) {
		String sql = "UPDATE tblReadingVerification SET result = ?, status = ?, closedAt = ? WHERE id = ?";
		try{
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, v.getResult());
			ps.setString(2, ReadingVerification.STATUS_CLOSED);
			ps.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			ps.setInt(4, v.getId());
			ps.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
