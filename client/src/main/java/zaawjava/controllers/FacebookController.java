package zaawjava.controllers;

import DTO.UserDTO;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.web.WebView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;
import zaawjava.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class FacebookController implements Initializable {
    private ScreensManager screensManager;
    private UserService userService;
    private SocketService socketService;

    @FXML
    private WebView webView;

    @Autowired
    public void setSocketService(SocketService socketService) {
        this.socketService = socketService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    String domain = "https://ftims.edu.p.lodz.pl/";  //To strona na ktora bedzie redirect, moze trzeba jakos to lepiej wymyslic
    String appID = "1954377814849369"; //To jest ID naszej apki na stronie Facebook Developers
    String accessToken;
    String authUrl = "https://www.facebook.com/v2.9/dialog/oauth?client_id=" + appID + "&response_type=token&redirect_uri=" + domain + "&scope=" +
            "email,public_profile,user_birthday,user_location";
    FacebookClient facebookClient;
    User userFB;

    private void doAuthorizationByWebView() {
        webView.getEngine().load(authUrl);
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            String location = webView.getEngine().getLocation();
                            screensManager.getStage().setTitle(location);
                            if (!location.contains("facebook.com")) {
                                accessToken = location.replaceAll(".*#access_token=(.+)&.*", "$1");
                                System.out.println(accessToken);
                                try {
                                    facebookClient = new DefaultFacebookClient(accessToken, Version.VERSION_2_8);
                                    userFB = facebookClient.fetchObject("me", com.restfb.types.User.class, Parameter.with("fields", "first_name,last_name,gender,name,picture,email,birthday,location"));
//                                    System.out.println(userFB);
                                    doRegistration();
                                } catch (FacebookOAuthException ex) {
                                    Platform.runLater(() -> showErrorMessage("FB error: " + ex.getErrorType() + " " + ex.getErrorMessage()));
                                    System.out.println("FB error: " + ex.getErrorType() + " " + ex.getErrorMessage());
                                }

                            }
                        }
                    }
                });
    }

    private void doRegistration() {
        UserDTO userNew = new UserDTO(userFB.getEmail(), "pass", userFB.getFirstName(), userFB.getLastName(), Utils.parseFB(userFB.getBirthday()), userFB.getGender());
        String userPhoto = userFB.getPicture().getUrl();
        NamedFacebookType userLocation = userFB.getLocation();
        String userAddress = "";
        if (userLocation != null) {
            userAddress = userLocation.getName();
        }
        if (userPhoto != null) userNew.setPhoto(userPhoto);
        if (userAddress != null && userLocation != null) userNew.setAddress(userLocation.getName());
        socketService.emit("onRegistration", userNew).whenComplete((msg, ex) -> {
            if (ex == null) {
                if ("registered".equals(msg)) {
                    Platform.runLater(() -> {
                        try {
                            socketService.disconnect();
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.initOwner(screensManager.getStage());
                            alert.setTitle("Information about your Account password");
                            alert.setHeaderText("Your default password is 'pass'");
                            alert.setContentText("Please change your password in 'Profil'!");
                            alert.showAndWait();
                            screensManager.goToLoginView();
                        } catch (IOException e) {
                            e.printStackTrace();
                            Platform.runLater(() -> showErrorMessage("Cannot load login view"));
                            System.out.println("Cannot load login view");
                        }
                    });
                } else {
                    Platform.runLater(() -> showErrorMessage("Registration failed during writing data to database. " + msg));
                    System.out.println("Registration failed during writing data to database. " + msg);
                }
            } else {
                Platform.runLater(() -> showErrorMessage("Registration failed during connection to database"));
                System.out.println("Registration failed during connection to database");
            }
        });
    }

    private void showErrorMessage(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(screensManager.getStage());
        alert.setTitle("WAMY error");
        alert.setHeaderText("Please correct invalid fields or contact with administrator");
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void goToApp(ActionEvent event) {
        try {
            screensManager.goToLoginView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        doAuthorizationByWebView();
    }
}
