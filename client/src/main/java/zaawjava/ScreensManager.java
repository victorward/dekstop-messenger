package zaawjava;


import io.netty.channel.ChannelFuture;
import javafx.application.Platform;
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

    private Boolean connecting = false;

    public Stage getStage() {
        return stage;
    }

    private Stage stage;

    private final ApplicationContext ctx;

    private final SpringFxmlLoader loader;


    private LoginController loginController;
    private MainViewController mainViewController;
    private SocketService socketService;

    @Autowired
    public ScreensManager(ApplicationContext ctx) {
        this.ctx = ctx;
        loader = new SpringFxmlLoader(ctx);
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

    public void init() {
        if (connecting) {
            return;
        }
        connecting = true;
        log.debug("Trying to connect...");
        Platform.runLater(() ->loginController.getMessageLabel().setText("Connecting..."));
        socketService.connect().addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                log.debug("Connected with server");
                Platform.runLater(() -> loginController.getMessageLabel().setText("Connected."));
                connecting = false;
            } else {
                log.debug("Connection error. Please check server configuration");
                Platform.runLater(() -> loginController.getMessageLabel().setText("Connection error. Please check server configuration"));
                Platform.runLater(() -> loginController.getLogin().setDisable(true));
                Platform.runLater(() -> loginController.getRegistration().setDisable(true));
                connecting = false;
            }
        });
        stage.setOnCloseRequest(event1 -> {
            log.debug("closing window...");
            socketService.disconnect();
        });

    }

    public void setPrimaryStage(Stage stage) {
        this.stage = stage;
    }

    public void goToLoginView() throws IOException {
        String fxmlFile = "/fxml/login.fxml";

        Parent rootNode = (Parent) loader.load(fxmlFile);

        Scene scene = new Scene(rootNode, 400, 200);
        scene.getStylesheets().add("/styles/styles.css");

        stage.setTitle("Login");
        stage.setScene(scene);

    }

    public void goToMainView() {

        Parent rootNode = (Parent) loader.load("/fxml/mainView.fxml");
        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
    }

    public void goToRegistrationView() {
        Parent rootNode = (Parent) loader.load("/fxml/registration.fxml");
        Scene scene = new Scene(rootNode);

        stage.setScene(scene);
    }

    public void goToProfileView() {
        Parent rootNode = (Parent) loader.load("/fxml/profile.fxml");
        Scene scene = new Scene(rootNode);
        stage.setScene(scene);

    }
}
