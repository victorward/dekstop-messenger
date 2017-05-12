package zaawjava;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import zaawjava.model.Conversation;
import zaawjava.model.Country;
import zaawjava.model.Language;
import zaawjava.model.ChatMessage;
import zaawjava.model.User;
import zaawjava.services.DatabaseConnector;

public class Main {
    public static SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(Country.class).addAnnotatedClass(Language.class).addAnnotatedClass(Conversation.class).addAnnotatedClass(ChatMessage.class).buildSessionFactory();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        Server server = applicationContext.getBean(Server.class);

//        DatabaseConnector connector = new DatabaseConnector();
//        List<ChatMessage> ha = connector.getMessageListByUsers(connector.getByEmail("dodawany5@dodany.dod"), connector.getByEmail("JakiesTakie@Zmienione.pl"));
//        Conversation conversation = connector.getConversationByUsers(connector.getByEmail("Insertowy1@insert.gg"),connector.getByEmail("JakiesTakie@Zmienione.pl"));
//        System.out.println(conversation.toString());
//        connector.insertPrivateMessage(new ChatMessage(conversation,"Yo",connector.getByEmail("dodawany5@dodany.dod")));

        server.run();
//        new Server().run();
    }
}
