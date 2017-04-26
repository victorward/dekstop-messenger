package zaawjava.services;

import model.Country;
import model.Language;
import model.User;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import zaawjava.Main;

@Service
public class DatabaseConnector {
    private Session session;

    // TEST METHODS
    public User getUser(String narazieNieWazne) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        User currentUser = session.get(User.class, 1);
        Hibernate.initialize(currentUser.getLanguages());
        session.getTransaction().commit();
        return currentUser;
    }
    public Language getLanguage(String narazieNieWazne) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        Language currentLanguage = session.get(Language.class, 1);
        session.getTransaction().commit();
        return currentLanguage;
    }
    public Country getCountry(int countryID) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        Country currentCountry = session.get(Country.class, countryID);
        session.getTransaction().commit();
        return currentCountry;
    }
    // FINISH HERE

    public User getByEmail(String email)
    {
    	User currentUser;
		session = Main.factory.getCurrentSession();
		session.beginTransaction();
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
		Root<User> root = criteriaQuery.from(User.class);
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
		try
		{
			currentUser = session.createQuery(criteriaQuery).getSingleResult();
		}
		catch (NoResultException nre)
		{
			session.getTransaction().commit();
			return null;
		}
		Hibernate.initialize(currentUser.getLanguages());
		session.getTransaction().commit();
		return currentUser;
    }
    public void insertUser(User user)
    {
		session = Main.factory.getCurrentSession();
		session.beginTransaction();
		session.save(user);
		session.getTransaction().commit();
    }
    public void updateUser(User user)
    {
    	session = Main.factory.getCurrentSession();
		session.beginTransaction();
		session.update(user);
		session.getTransaction().commit();
		session.close();
    }
}
