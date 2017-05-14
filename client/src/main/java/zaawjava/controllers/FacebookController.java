package zaawjava.controllers;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.exception.FacebookOAuthException;
import com.restfb.types.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;

import java.io.IOException;

@Component
public class FacebookController {
    private ScreensManager screensManager;
    private UserService userService;
    private final SocketService socketService;

    @FXML
    private WebView webView;

    @Autowired
    public FacebookController(SocketService socketService) {
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

    String domain = "https://github.com/sikora195703/jcom";  //To strona na ktora bedzie redirect, moze trzeba jakos to lepiej wymyslic
    String appID = "1954377814849369"; //To jest ID naszej apki na stronie Facebook Developers
    String accessToken;

    String authUrl = "https://www.facebook.com/v2.9/dialog/oauth?client_id=" + appID + "&response_type=token&redirect_uri=" + domain + "&scope=" +
            "email";
//                "email,first_name,last_name,gender";

    //                + "birthday,email,first_name,gender,languages,last_name,locale,ads_management";
    FacebookClient facebookClient;
    User userFB;

    @FXML
    public void initialize() {
//        doAutorizatrionByChrome();
        doAuthorizationByWebView();
    }


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
                                System.out.println("Access token:\n" + accessToken);
                                try {
                                    facebookClient = new DefaultFacebookClient(accessToken);
                                    userFB = facebookClient.fetchObject("me", com.restfb.types.User.class);
                                    System.out.println(userFB);
                                } catch (FacebookOAuthException ex) {
                                    System.out.println("FB error" + ex.getErrorType() + " " + ex.getErrorMessage());
                                }

                            }
                        }
                    }
                });
    }

    private void doAutorizatrionByChrome() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(authUrl);
        while (true) { //Sprawdzamy czy uzytkownik jest na stronie facebooka
            if (webDriver.getCurrentUrl() != null) {
                if (!webDriver.getCurrentUrl().contains("facebook.com")) {
                    String url = webDriver.getCurrentUrl();
                    accessToken = url.replaceAll(".*#access_token=(.+)&.*", "$1");
                    webDriver.quit();
                    System.out.println("Access token:\n" + accessToken);
                    FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
                    User userFB = facebookClient.fetchObject("me", com.restfb.types.User.class);
                    System.out.println(userFB);
                    return;
                }
            }
        }
    }

    @FXML
    void goToApp(ActionEvent event) {
        try {
            screensManager.goToLoginView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
