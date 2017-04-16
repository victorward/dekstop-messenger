package zaawjava.services;

import model.Country;
import model.Language;
import model.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import zaawjava.Main;

@Service
public class DatabaseConnector {
    private static Session session;

    // TEST METHODS
    public static User getUser(String narazieNieWazne) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        User currentUser = session.get(User.class, 1);
        session.getTransaction().commit();
        return currentUser;
    }
    public static Language getLanguage(String narazieNieWazne) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        Language currentLanguage = session.get(Language.class, 1);
        session.getTransaction().commit();
        return currentLanguage;
    }
    public static Country getCountry(String narazieNieWazne) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        Country currentCountry = session.get(Country.class, 1);
        session.getTransaction().commit();
        return currentCountry;
    }
    // FINISH HERE

    public static User getByEmail(String email)
    {
		session = Main.factory.getCurrentSession();
		session.beginTransaction();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
		User currentUser = session.createQuery(criteriaQuery).getSingleResult();
		session.getTransaction().commit();
		return currentUser;
    }
    public static void insertUser(User user)
    {
		session = Main.factory.getCurrentSession();
		session.beginTransaction();
		user.setCountryId(1);
		session.save(user);
		session.getTransaction().commit();
    }
}
