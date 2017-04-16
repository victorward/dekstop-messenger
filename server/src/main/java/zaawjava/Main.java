package zaawjava;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import model.User;
import zaawjava.model.DatabaseConnector;

public class Main{
	public static SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).buildSessionFactory();

    public static void main(String[] args){

    	//DatabaseConnector test = new DatabaseConnector();
    	User testUser = DatabaseConnector.getUser("");
    	System.out.println(testUser.toString());
        new Server().run();
    }
}
