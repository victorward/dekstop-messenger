package zaawjava;


import DTO.UserDTO;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import zaawjava.controllers.LoginController;
import zaawjava.controllers.MainViewController;
import zaawjava.controllers.UserToUserController;
import zaawjava.services.SocketService;
import zaawjava.utils.SpringFxmlLoader;

import java.io.IOException;

@Component
public class ScreensManager {
    private static final Logger log = LoggerFactory.getLogger(ScreensManager.class);


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

    private UserToUserController userToUserController;
    @Autowired
    public void setUserToUserController(UserToUserController userToUserController) {
        this.userToUserController = userToUserController;
    }

    public void init() {
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

    public void goToUserToUserView(UserDTO userDTO) {
        mainViewController.getContentPane().getChildren().clear();
        Parent rootNode = (Parent) loader.load("/fxml/userToUser.fxml");
        //dziala, ale hrenowo
        userToUserController.setUserDTO(userDTO);
        mainViewController.getContentPane().getChildren().removeAll();
        mainViewController.getContentPane().getChildren().add(rootNode);
    }

    public void goToProfileView() {
        Parent rootNode = (Parent) loader.load("/fxml/profile.fxml");
        mainViewController.getContentPane().getChildren().removeAll();
        mainViewController.getContentPane().getChildren().add(rootNode);
    }
}
