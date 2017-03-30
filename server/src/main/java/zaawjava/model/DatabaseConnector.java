package zaawjava.model;

import java.sql.Connection;
import java.sql.DriverManager;

import org.hibernate.Session;

import zaawjava.Main;
import model.User;

public class DatabaseConnector
{
	private static Session session;
	public static User getUser(String narazieNieWazne)
	{
		session = Main.factory.getCurrentSession();
		session.beginTransaction();
		User currentUser = session.get(User.class, 1);
		session.getTransaction().commit();
		return currentUser;
	}
}
