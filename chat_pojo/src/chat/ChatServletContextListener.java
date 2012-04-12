package chat;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import chat.persistence.ChatDao;
import chat.persistence.RegistrationDao;

public class ChatServletContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		Context initCtx = null;
		Context envCtx = null;
		try {
			initCtx = new InitialContext();
			envCtx = (Context) initCtx.lookup("java:comp/env");
			DataSource ds = (DataSource) envCtx.lookup("jdbc/chat");
			RegistrationDao regDao = new RegistrationDao(ds);
			ChatDao chatDao = new ChatDao(ds);
			event.getServletContext().setAttribute("regDao", regDao);
			event.getServletContext().setAttribute("chatDao", chatDao);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
