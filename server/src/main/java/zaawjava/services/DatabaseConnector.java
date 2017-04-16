package zaawjava.services;

import model.User;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import zaawjava.Main;

@Service
public class DatabaseConnector {
    private static Session session;

    public static User getUser(String narazieNieWazne) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        User currentUser = session.get(User.class, 1);
        session.getTransaction().commit();
        return currentUser;
    }
}
