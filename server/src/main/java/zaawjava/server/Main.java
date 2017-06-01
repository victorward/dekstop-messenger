package zaawjava.server;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import zaawjava.server.model.Conversation;
import zaawjava.server.model.Country;
import zaawjava.server.model.Language;
import zaawjava.server.model.ChatMessage;
import zaawjava.server.model.User;

public class Main {
    public static SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(Country.class).addAnnotatedClass(Language.class).addAnnotatedClass(Conversation.class).addAnnotatedClass(ChatMessage.class).buildSessionFactory();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        Server server = applicationContext.getBean(Server.class);

//        DatabaseConnector connector = new DatabaseConnector();
//        List<ChatMessage> ha = connector.getMessageListByUsers(connector.getUserByEmail("dodawany5@dodany.dod"), connector.getUserByEmail("JakiesTakie@Zmienione.pl"));
//        Conversation conversation = connector.getConversationByUsers(connector.getUserByEmail("Insertowy1@insert.gg"),connector.getUserByEmail("JakiesTakie@Zmienione.pl"));
//        System.out.println(conversation.toString());
//        connector.insertPrivateMessage(new ChatMessage(conversation,"Yo",connector.getUserByEmail("dodawany5@dodany.dod")));

        server.run();
//        new Server().run();
    }
}
