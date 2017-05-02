package zaawjava;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import zaawjava.model.Country;
import zaawjava.model.Language;
import zaawjava.model.User;

public class Main {
    public static SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(Country.class).addAnnotatedClass(Language.class).buildSessionFactory();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        Server server = applicationContext.getBean(Server.class);

        server.run();


//        new Server().run();
    }
}
