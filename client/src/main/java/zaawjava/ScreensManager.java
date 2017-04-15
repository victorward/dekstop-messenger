package zaawjava;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import zaawjava.controllers.LoginController;
import zaawjava.utils.SpringFxmlLoader;

import javax.swing.*;
import java.io.IOException;

@Component
public class ScreensManager {
    private Stage stage;

    private ApplicationContext ctx;
    private LoginController loginController;

    @Autowired
    public ScreensManager(ApplicationContext ctx) {
        this.ctx = ctx;
    }
    @Autowired
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }



    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public void showLoginView() throws IOException {
        String fxmlFile = "/fxml/login.fxml";
        SpringFxmlLoader loader = new SpringFxmlLoader(ctx);

        Parent rootNode = (Parent) loader.load(fxmlFile);
        loginController.setParameters(stage);

        Scene scene = new Scene(rootNode, 400, 200);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Login");
        stage.setScene(scene);

        stage.show();

    }
}
