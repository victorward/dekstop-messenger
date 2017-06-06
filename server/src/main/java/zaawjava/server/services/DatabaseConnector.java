package zaawjava.server.services;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import zaawjava.commons.DTO.ChatMessageDTO;
import zaawjava.commons.DTO.CountryDTO;
import zaawjava.commons.DTO.LanguageDTO;
import zaawjava.commons.DTO.UserDTO;
import zaawjava.commons.utils.CryptoUtils;
import zaawjava.server.Main;
import zaawjava.server.ServerConnectionsHandler;
import zaawjava.server.Utils.DTOUtils;
import zaawjava.server.model.*;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class DatabaseConnector {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnector.class);

    private Session session;

    // TEST METHODS
//    public User getUser(String narazieNieWazne) {
//        session = Main.factory.getCurrentSession();
//        session.beginTransaction();
//        User currentUser = session.get(User.class, 1);
//        Hibernate.initialize(currentUser.getLanguages());
//        session.getTransaction().commit();
//        return currentUser;
//    }
//
//    public Language getLanguage(String narazieNieWazne) {
//        session = Main.factory.getCurrentSession();
//        session.beginTransaction();
//        Language currentLanguage = session.get(Language.class, 1);
//        session.getTransaction().commit();
//        return currentLanguage;
//    }
//
//    public Country getCountry(int countryID) {
//        session = Main.factory.getCurrentSession();
//        session.beginTransaction();
//        Country currentCountry = session.get(Country.class, countryID);
//        session.getTransaction().commit();
//        return currentCountry;
//    }
//
//    public Country getCountryObjectByID(String countryName) {
//        session = Main.factory.getCurrentSession();
//        session.beginTransaction();
//        Country currentCountry = session.get(Country.class, countryName);
//        session.getTransaction().commit();
//        return currentCountry;
//    }
    // FINISH HERE

    public UserDTO getUserByEmail(String email) {
        User currentUser;
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);
        criteriaQuery.where(criteriaBuilder.equal(root.get("email"), email));
        try {
            currentUser = session.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException nre) {
            session.getTransaction().commit();
            return null;
        }
        Hibernate.initialize(currentUser.getLanguages());
        session.getTransaction().commit();
        session.close();
        return DTOUtils.convertUserToDTO(currentUser);
    }

    public void insertUser(UserDTO user) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        session.save(DTOUtils.convertDTOtoUser(user));
        session.getTransaction().commit();
        session.close();

    }

    public void updateUser(UserDTO user) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        session.update(DTOUtils.convertDTOtoUser(user));
        session.getTransaction().commit();
        session.close();
    }

    public List<UserDTO> getAllUsers() {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        CriteriaQuery<User> criteriaQuery = session.getCriteriaBuilder().createQuery(User.class);
        criteriaQuery.from(User.class);
        List<User> listOfUsers = session.createQuery(criteriaQuery).getResultList();
        session.getTransaction().commit();
        session.close();

        return DTOUtils.convertUserToDTO(listOfUsers);
    }

    public List<LanguageDTO> getLanguages() {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        CriteriaQuery<Language> criteriaQuery = session.getCriteriaBuilder().createQuery(Language.class);
        criteriaQuery.from(Language.class);
        List<Language> listOfLanguages = session.createQuery(criteriaQuery).getResultList();
        session.getTransaction().commit();
        session.close();

        return DTOUtils.convertLanguageToDTO(listOfLanguages);
    }

    public List<CountryDTO> getCountries() {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        CriteriaQuery<Country> criteriaQuery = session.getCriteriaBuilder().createQuery(Country.class);
        criteriaQuery.from(Country.class);
        List<Country> listOfCountries = session.createQuery(criteriaQuery).getResultList();
        session.getTransaction().commit();
        session.close();

        return DTOUtils.convertCountryToDTO(listOfCountries);
    }

    public Conversation getConversation(int conversationId) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        Conversation currentConversation = session.get(Conversation.class, conversationId);
        Hibernate.initialize(currentConversation.getPrivateMessages());
        session.getTransaction().commit();
        session.close();

        return currentConversation;
    }

    public void saveConversation(Conversation conversation) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        session.saveOrUpdate(conversation);
        session.getTransaction().commit();
        session.close();

    }

    public void insertPrivateMessage(ChatMessage privateMessage) {
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        session.save(privateMessage);
        session.getTransaction().commit();
        session.close();

    }

    public Conversation getConversation(UserDTO user1, UserDTO user2) {
        if (user1 == null || user2 == null) throw new NullPointerException("User cannot be null");
        Conversation selectedConversation = null;
        session = Main.factory.getCurrentSession();
        session.beginTransaction();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Conversation> criteriaQueryConversation = criteriaBuilder.createQuery(Conversation.class);
        Root<Conversation> rootConversation = criteriaQueryConversation.from(Conversation.class);
        criteriaQueryConversation.select(rootConversation);
        criteriaQueryConversation.where(
                criteriaBuilder.and(criteriaBuilder.equal(rootConversation.get("user1"), user1.getId())),
                (criteriaBuilder.equal(rootConversation.get("user2"), user2.getId())));

        try {
            selectedConversation = session.createQuery(criteriaQueryConversation).getSingleResult();
        } catch (NoResultException e) {
            session.getTransaction().commit();
            return null;
        }
        Hibernate.initialize(selectedConversation.getPrivateMessages());
        session.getTransaction().commit();
        session.close();

        return selectedConversation;
    }

    public Conversation getConversationByUsers(UserDTO user1, UserDTO user2) {
        Conversation conversation = getConversation(user1, user2);
        if (conversation != null) {
            return conversation;
        } else {
            conversation = getConversation(user2, user1);
            if (conversation != null) {
                return conversation;
            } else {
                Conversation newConversation = new Conversation(DTOUtils.convertDTOtoUser(user1), DTOUtils.convertDTOtoUser(user2));
                saveConversation(newConversation);
                return newConversation;
            }
        }
    }

    public List<ChatMessageDTO> getMessageListByUsers(UserDTO user1, UserDTO user2) {
        Conversation conversation = getConversation(user1, user2);
        List<ChatMessage> messages;
        if (conversation != null) {
            messages = conversation.getPrivateMessages();
        } else {
            conversation = getConversation(user2, user1);
            if (conversation != null) {
                messages = conversation.getPrivateMessages();
            } else {
                Conversation newConversation = new Conversation(DTOUtils.convertDTOtoUser(user1), DTOUtils.convertDTOtoUser(user2));
                saveConversation(newConversation);
                messages = newConversation.getPrivateMessages();
            }
        }
        return DTOUtils.convertChatMessageToDTO(messages);
    }

    public boolean addNewUser(UserDTO user) {
        try {
            if (user.getAddress() == null || user.getAddress().length() < 1)
                user.setAddress("");
            if (user.getPhoto() == null || user.getPhoto().length() < 1)
                user.setPhoto("");
            user.setPassword(CryptoUtils.encryptPassword(user.getPassword()));
            log.debug("Trying add to database" + user);
            insertUser(user);
            return true;
        } catch (Exception ex) {
            log.warn("Adding user failed", ex);
            return false;
        }
    }
}
