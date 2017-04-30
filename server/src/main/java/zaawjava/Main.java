package zaawjava;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import zaawjava.model.Country;
import zaawjava.model.Language;
import zaawjava.model.User;
import zaawjava.services.DatabaseConnector;

public class Main {
    public static SessionFactory factory = new Configuration().configure("hibernate.cfg.xml").addAnnotatedClass(User.class).addAnnotatedClass(Country.class).addAnnotatedClass(Language.class).buildSessionFactory();

    public static void main(String[] args) {

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        Server server = applicationContext.getBean(Server.class);

//        DatabaseConnector databaseConnector = new DatabaseConnector();
//        List<Country> allCountries = databaseConnector.getCountries();
//        for (Country c : allCountries)
//        {
//        	System.out.println(c.getCountryName());
//        }
//        List<Language> allLanguages = databaseConnector.getLanguages();
//        for (Language c : allLanguages)
//        {
//        	System.out.println(c.getLanguageName());
//        }
//        User testUser = databaseConnector.getUser("");
//        System.out.println(testUser.toString());
//        System.out.println(testUser.getLanguages().toArray(new Language[testUser.getLanguages().size()])[0].getLanguageName());
//        System.out.println(testUser.getLanguages().toArray(new Language[testUser.getLanguages().size()])[1].getLanguageName());
//        System.out.println(testUser.getLanguages().toArray(new Language[testUser.getLanguages().size()])[2].getLanguageName());
//        Set<Language> languages = new HashSet<Language>(0);
//        languages.add(testUser.getLanguages().toArray(new Language[testUser.getLanguages().size()])[2]);
//        User dodawanyUser = new User(languages, "Adam", "Jakis", 56624456, "male", "dodawany5@dodany.dod", "1", "Dodawarska 25", new Country(1 ,"Polska"), LocalDate.now(), "Nie ma");
//        databaseConnector.insertUser(dodawanyUser);
//        testUser.setEmail("JakiesTakie@Zmienione.pl");
//        databaseConnector.updateUser(testUser);
//        System.out.println(testUser.getCountry().getCountryName());
//        Country testCountry = databaseConnector.getCountry(1);
//        System.out.println(testCountry.toString());
//        Language testLanguage = databaseConnector.getLanguage("");
//        System.out.println(testLanguage.toString());
//        User testUser2 = databaseConnector.getByEmail("Kamil@haggg.gg");
//        if(testUser2 == null)
//        {
//        	System.out.println("jest ok");
//        }
//        User testUser3 = databaseConnector.getByEmail("Kamil@ggg.gg");
//        System.out.println(testUser3.toString());
//        User testInsertUser = new User("Insertowy1@insert.gg", "nieSpodziewalbysSieTakiego");
//        databaseConnector.insertUser(testInsertUser);
        server.run();


//        new Server().run();
    }
}
