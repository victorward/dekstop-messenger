package zaawjava;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import zaawjava.controllers.LoginController;
import zaawjava.utils.SpringFxmlLoader;

public class MainApp extends Application {

    private static final Logger log = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    public void start(Stage stage) throws Exception {

//        SpringFxmlLoader loader = new SpringFxmlLoader();
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringApplicationConfig.class);
        ScreensManager sm = applicationContext.getBean(ScreensManager.class);
        sm.setPrimaryStage(stage);

        sm.showLoginView();

//        String fxmlFile = "/fxml/login.fxml";
//
//
//        Parent rootNode = (Parent) loader.load(fxmlFile);
//        LoginController c = (LoginController) loader.getController();
//        c.setParameters(stage);
//
//        Scene scene = new Scene(rootNode, 400, 200);
//        scene.getStylesheets().add("/styles/styles.css");
//
//        stage.setTitle("Login");
//        stage.setScene(scene);
//        stage.setWidth(scene.getWidth());
//        stage.setHeight(scene.getHeight());


//        stage.show();


    }
}
