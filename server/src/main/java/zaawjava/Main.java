package zaawjava;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import model.User;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).buildSessionFactory();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        Server server = applicationContext.getBean(Server.class);

        server.run();


        //DatabaseConnector test = new DatabaseConnector();
//    	User testUser = DatabaseConnector.getUser("");
//    	System.out.println(testUser.toString());
//        new Server().run();
    }
}
