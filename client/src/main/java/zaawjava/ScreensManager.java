package zaawjava;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import zaawjava.controllers.LoginController;
import zaawjava.controllers.MainViewController;
import zaawjava.services.SocketService;
import zaawjava.utils.SpringFxmlLoader;

import java.io.IOException;

@Component
public class ScreensManager {
    private static final Logger log = LoggerFactory.getLogger(ScreensManager.class);


    private Stage stage;

    private ApplicationContext ctx;

    private LoginController loginController;
    private MainViewController mainViewController;
    private SocketService socketService;

    @Autowired
    public ScreensManager(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Autowired
    public void setLoginController(LoginController loginController) {
        this.loginController = loginController;
    }

    @Autowired
    public void setSocketService(SocketService socketService) {
        this.socketService = socketService;
    }

    @Autowired
    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public void showLoginView() throws IOException {
        String fxmlFile = "/fxml/login.fxml";
        SpringFxmlLoader loader = new SpringFxmlLoader(ctx);

        Parent rootNode = (Parent) loader.load(fxmlFile);

        Scene scene = new Scene(rootNode, 400, 200);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Login");
        stage.setScene(scene);

        stage.setOnCloseRequest(event1 -> {
            log.debug("closing window...");
            socketService.disconnect();
        });

    }

    public void goToMainView() {
        SpringFxmlLoader loader = new SpringFxmlLoader(ctx);

        Parent rootNode = (Parent) loader.load("/fxml/mainView.fxml");
        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
    }
}
