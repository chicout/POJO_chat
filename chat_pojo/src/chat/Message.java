package chat;

import java.util.Date;

/**
 * Simple <code>Message</code> class to hold message's data.
 * @author Pavel Shitikov
 *
 */
public class Message {
	private String username;
	private String message;
	private Date datetime;
	
	/**
	 * Constructs new <code>Message</code> with given parameters.
	 * @param username user's login;
	 * @param message the message user sent;
	 * @param datetime date ant time of sending the message.
	 */
	public Message(String username, String message, Date datetime) {
		this.username = username;
		this.message = message;
		this.datetime = datetime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	
}
