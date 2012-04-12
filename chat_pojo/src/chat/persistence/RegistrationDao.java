package chat.persistence;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * The <code>RegistrationDao</code> class represents a simple data access object
 * for performing user's registration and validation.
 * 
 * @author Pavel Shitikov
 * @see ChatDao
 */
public class RegistrationDao extends AbstractDao {

	/**
	 * Create a new DAO for registration and validation.
	 * 
	 * @param ds
	 */
	public RegistrationDao(DataSource ds) {
		super(ds);
	}

	/**
	 * 
	 * @return <code>true</code> if there is no such user and the registration
	 *         completed successfully; <code>false</code> if the user already
	 *         exists.
	 * @param username
	 *            user's login.
	 * @param password
	 *            user's password.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 */
	public final boolean registerUser(String username, String password)
			throws SQLException {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement insertUser = null;
		PreparedStatement existUser = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			existUser = conn
					.prepareStatement("select user_name from user where user_name = ?");
			existUser.setString(1, username);
			existUser.execute();
			rs = existUser.getResultSet();
			if (!rs.next()) {
				insertUser = conn
						.prepareStatement("insert into user values (?, md5(?), 0)");
				insertUser.setString(1, username);
				insertUser.setString(2, password);
				insertUser.executeUpdate();
				flag = true;
			}
			conn.commit();
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (insertUser != null)
				insertUser.close();
			if (existUser != null)
				existUser.close();
			if (conn != null)
				conn.close();
		}
		return flag;
	}

	/**
	 * 
	 * @return <code>true</code> if the validation completed successfully;
	 *         <code>false</code> otherwise.
	 * @param username
	 *            user's login.
	 * @param password
	 *            user's password.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 */
	public final boolean validateUser(String username, String password)
			throws SQLException {
		boolean flag = false;
		Connection conn = null;
		PreparedStatement existUser = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			existUser = conn
					.prepareStatement("select user_name, user_password from user where user_name = ?");
			existUser.setString(1, username);
			existUser.execute();
			rs = existUser.getResultSet();
			if (rs.next() && md5(password).equals(rs.getString(2))) {
				flag = true;
			}
			conn.commit();
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (existUser != null)
				existUser.close();
			if (conn != null)
				conn.close();
		}
		return flag;
	}

	private static String md5(String source) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes = md.digest(source.getBytes("UTF-8"));
			return getString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			String hex = Integer.toHexString((int) 0x00FF & b);
			if (hex.length() == 1) {
				sb.append("0");
			}
			sb.append(hex);
		}
		return sb.toString();
	}

}
