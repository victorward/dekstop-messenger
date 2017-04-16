package zaawjava;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import model.Country;
import model.Language;
import model.User;
import zaawjava.services.DatabaseConnector;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(Country.class).addAnnotatedClass(Language.class).buildSessionFactory();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        Server server = applicationContext.getBean(Server.class);

//        User testUser = DatabaseConnector.getUser("");
//        System.out.println(testUser.toString());
//        Country testCountry = DatabaseConnector.getCountry("");
//        System.out.println(testCountry.toString());
//        Language testLanguage = DatabaseConnector.getLanguage("");
//        System.out.println(testLanguage.toString());
//        User testUser2 = DatabaseConnector.getByEmail("Kamil@ggg.gg");
//        System.out.println(testUser2.toString());
//        User testInsertUser = new User("Insertowy@insert.gg", "nieSpodziewalbysSieTakiego");
//        DatabaseConnector.insertUser(testInsertUser);
        server.run();


//        new Server().run();
    }
}
