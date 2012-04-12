package chat.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import chat.Message;

/**
 * The <code>ChatDao</code> class represents a simple data access object for
 * performing chat data actions;
 * 
 * @author Pavel Shitikov
 * @see RegistrationDao
 */
public class ChatDao extends AbstractDao {

	/**
	 * Constructor to create a new <code>ChatDao</code>.
	 * 
	 * @param ds
	 */
	public ChatDao(DataSource ds) {
		super(ds);
	}

	/**
	 * Inserts data in the <code>message</code> table.
	 * 
	 * @param name
	 *            user's name;
	 * @param text
	 *            user's message;
	 * @param datetime
	 *            date and time of the message.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 */
	public void insertMessage(String name, String text, Date datetime)
			throws SQLException {
		if (name == null || text == null || datetime == null)
			return;
		Connection conn = null;
		PreparedStatement insMsgStmt = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			insMsgStmt = conn
					.prepareStatement("insert into message values (?, ?, ?)");
			insMsgStmt.setString(1, name);
			insMsgStmt.setString(2, text);
			java.sql.Timestamp timestamp = new java.sql.Timestamp(
					datetime.getTime());
			insMsgStmt.setTimestamp(3, timestamp);
			insMsgStmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			e.printStackTrace();
		} finally {
			if (insMsgStmt != null)
				insMsgStmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * Returns currently online users.
	 * 
	 * @return list of online users. If there aren't online users returns
	 *         <code>null</code>.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 */
	public List<String> getOnlineUsers() throws SQLException {
		List<String> list = new ArrayList<>();
		Connection conn = null;
		Statement getOnlineStmt = null;
		ResultSet rs = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			getOnlineStmt = conn.createStatement();
			rs = getOnlineStmt
					.executeQuery("select user_name from user where user_status = 1");
			while (rs.next()) {
				String username = rs.getString(1);
				list.add(username);
			}
			conn.commit();
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (getOnlineStmt != null)
				getOnlineStmt.close();
			if (conn != null)
				conn.close();
		}
		return list.isEmpty() ? null : list;
	}

	/**
	 * Returns list of new messages from the specified date and time.
	 * 
	 * @param datetime
	 * @return list of <code>Message</code> objects.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 * @see Message
	 */
	public List<Message> getNewMessages(Date datetime) throws SQLException {
		List<Message> list = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement getNewMessages = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			getNewMessages = conn
					.prepareStatement("select * from message where mes_time > ?");
			java.sql.Timestamp timestamp = new java.sql.Timestamp(
					datetime.getTime());
			getNewMessages.setTimestamp(1, timestamp);
			rs = getNewMessages.executeQuery();
			while (rs.next()) {
				String username = rs.getString(1);
				String mes = rs.getString(2);
				timestamp = rs.getTimestamp(3);
				Date date = new Date(timestamp.getTime());
				Message message = new Message(username, mes, date);
				list.add(message);
			}
			conn.commit();
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (getNewMessages != null)
				getNewMessages.close();
			if (conn != null)
				conn.close();
		}
		return list.isEmpty() ? null : list;
	}

	/**
	 * Returns list of last new messages.
	 * 
	 * @param count
	 * @return list of <code>Message</code> objects.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 * @see Message
	 */
	public List<Message> getNewMessages(int count) throws SQLException {
		List<Message> list = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement getNewMessages = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			getNewMessages = conn
					.prepareStatement("select * from (select * from message order by mes_time desc limit ?) as user order by mes_time");
			getNewMessages.setInt(1, count);
			rs = getNewMessages.executeQuery();
			java.sql.Timestamp timestamp = null;
			while (rs.next()) {
				String username = rs.getString(1);
				String mes = rs.getString(2);
				timestamp = rs.getTimestamp(3);
				Date date = new Date(timestamp.getTime());
				Message message = new Message(username, mes, date);
				list.add(message);
			}
			conn.commit();
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			e.printStackTrace();
		} finally {
			if (rs != null)
				rs.close();
			if (getNewMessages != null)
				getNewMessages.close();
			if (conn != null)
				conn.close();
		}
		return list.isEmpty() ? null : list;
	}

	/**
	 * Set specified user's status online.
	 * 
	 * @param username
	 *            User to set the status.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 */
	public void setOnline(String username) throws SQLException {
		setStatus(username, true);
	}

	/**
	 * Set specified user's status offline.
	 * 
	 * @param username
	 *            User to set the status.
	 * @throws SQLException
	 *             when rollback or closing Statement/ResultSet/Connection did
	 *             not succeed.
	 */
	public void setOffline(String username) throws SQLException {
		setStatus(username, false);
	}

	private void setStatus(String username, boolean flag) throws SQLException {
		int status = flag ? 1 : 0;
		Connection conn = null;
		PreparedStatement setOnlineStmt = null;
		try {
			conn = ds.getConnection();
			conn.setAutoCommit(false);
			setOnlineStmt = conn
					.prepareStatement("update user set user_status=? where user_name=?");
			setOnlineStmt.setInt(1, status);
			setOnlineStmt.setString(2, username);
			setOnlineStmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			if (conn != null)
				conn.rollback();
			e.printStackTrace();
		} finally {
			if (setOnlineStmt != null)
				setOnlineStmt.close();
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * Test dummy method to fill tables;
	 * @throws SQLException
	 */
	public void fillMessages() throws SQLException {
		long time = System.currentTimeMillis();
		int interval = 2000;
		Message[] mess = {
				new Message("Mami", "поиграйте в компютер", new Date(time)),
				new Message("Лесик", "сейчас, мами", new Date(time + interval)),
				new Message("Паша", "я сейчас найду тебя", new Date(time + 2
						* interval)),
				new Message("Женя", "а как?", new Date(time + 3 * interval)),
				new Message("Паша", "как?", new Date(time + 4 * interval)),
				new Message("Mami", "не ругайтесь, мальчики", new Date(time + 6
						* interval)),
				new Message("Женя", "мы не ругаемся, нам просто с Таней....",
						new Date(time + 8 * interval)),
				new Message("Лесик", "Владимир?",
						new Date(time + 20 * interval)),
				new Message("Женя", "ну... я тебя вычислю всё-таки наверное",
						new Date(time + 22 * interval)),
				new Message("Владимир", "это я говорю", new Date(time + 23
						* interval)),
				new Message("Женя", "Мне всё равно", new Date(time + 24
						* interval)),
				new Message("Лесик", "о чем это он?", new Date(time + 25
						* interval)),
				new Message("Паша", "ребята, я в это не играю", new Date(time
						+ 26 * interval)),
				new Message("Женя", "а зачем?", new Date(time + 27 * interval)),
				new Message("Паша", "а помнишь, как Мами тебя бомжом назвала?",
						new Date(time + 29 * interval)),
				new Message("Mami", "вы с ума сошли?", new Date(time + 30
						* interval)),
				new Message("Женя",
						"мы не с ума сошли, нам просто с Таней....", new Date(
								time + 32 * interval)),
				new Message("Лесик", "какой циплюнок?", new Date(time + 34
						* interval)),
				new Message("Женя", "ну... я тебя вычислю всё-таки наверное",
						new Date(time + 35 * interval)),
				new Message("Владимир", "наехал я, короче....", new Date(time
						+ 36 * interval)) };

		String[] queryUsers = { "insert into user values ('Mami', 123, 1)",
				"insert into user values ('Владимир', 123, 1)",
				"insert into user values ('Паша', 123, 1)",
				"insert into user values ('Женя', 123, 1)",
				"insert into user values ('Лесик', 123, 1)" };
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement prep = null;
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			prep = conn.prepareStatement("insert into message values(?, ?, ?)");
			for (int i = 0; i < queryUsers.length; i++) {
				stmt.executeUpdate(queryUsers[i]);
			}

			for (int i = 0; i < mess.length; i++) {
				prep.setString(1, mess[i].getUsername());
				prep.setString(2, mess[i].getMessage());
				java.sql.Timestamp timestmp = new java.sql.Timestamp(mess[i]
						.getDatetime().getTime());
				prep.setTimestamp(3, timestmp);
				prep.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (prep != null)
				prep.close();
			if (stmt != null)
				stmt.close();
			if (conn != null)
				conn.close();
		}
	}
	
	/**
	 * Method to erase the <code>user</code> table
	 * @throws SQLException
	 */
	public void eraseUsers() throws SQLException {
		eraseTable(false);
	}
	
	/**
	 * Method to erase the <code>message</code> table
	 * @throws SQLException
	 */
	public void eraseMessages() throws SQLException {
		eraseTable(true);
	}
	
	private void eraseTable(boolean flag) throws SQLException {
		Connection conn = null;
		Statement stmt = null;
		String table = flag ? "message" : "user"; 
		String query = "delete from " + table + " where true=true";
		try {
			conn = ds.getConnection();
			stmt = conn.createStatement();
			stmt.executeUpdate(query);	
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
}
