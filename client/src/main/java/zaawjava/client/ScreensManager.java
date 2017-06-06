package zaawjava.client;


import zaawjava.commons.DTO.UserDTO;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import zaawjava.client.controllers.LoginController;
import zaawjava.client.controllers.MainViewController;
import zaawjava.client.controllers.PrivateMessageController;
import zaawjava.client.services.SocketService;
import zaawjava.client.utils.SpringFxmlLoader;

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

    private PrivateMessageController privateMessageController;

    @Autowired
    public void setPrivateMessageController(PrivateMessageController privateMessageController) {
        this.privateMessageController = privateMessageController;
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
        Scene scene = new Scene(rootNode);
        stage.setTitle("Login");
        stage.setScene(scene);

    }

    public void goToMainView() {
        Parent rootNode = (Parent) loader.load("/fxml/mainView.fxml");
        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
        setGlobalChatView();
    }

    public void goToRegistrationView() {
        Parent rootNode = (Parent) loader.load("/fxml/registration.fxml");
        Scene scene = new Scene(rootNode);
        stage.setScene(scene);
    }

    public void setPrivateMessageController(UserDTO userDTO) {
        Parent rootNode = (Parent) loader.load("/fxml/privateMessage.fxml");
        mainViewController.getContentPane().getChildren().clear();
        privateMessageController.setUserDTO(userDTO);
        mainViewController.getContentPane().getChildren().add(rootNode);
    }

    public void setProfileView() {
        Parent rootNode = (Parent) loader.load("/fxml/profile.fxml");
        mainViewController.getContentPane().getChildren().clear();
        mainViewController.getContentPane().getChildren().add(rootNode);
    }

    public void setGlobalChatView() {
        Parent rootNode = (Parent) loader.load("/fxml/globalChat.fxml");
        mainViewController.getContentPane().getChildren().clear();
        mainViewController.getContentPane().getChildren().add(rootNode);
    }

    public void setLoginOAuth() {
        Parent rootNode = (Parent) loader.load("/fxml/facebookWeb.fxml");
        Scene scene = new Scene(rootNode);
        stage.setTitle("Facebook autorization");
        stage.setScene(scene);
    }

	public MainViewController getMainViewController() {
		return mainViewController;
	}

}
