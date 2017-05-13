package zaawjava.controllers;

import com.microsoft.alm.oauth2.useragent.AuthorizationException;
import com.microsoft.alm.oauth2.useragent.AuthorizationResponse;
import com.microsoft.alm.oauth2.useragent.UserAgent;
import com.microsoft.alm.oauth2.useragent.UserAgentImpl;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
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
import java.net.URI;
import java.net.URISyntaxException;

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
    String authUrl = "https://graph.facebook.com/oauth/authorize?type=user_agent&client_id=" + appID + "&redirect_uri=" + domain + "&scope=user_about_me,"
            + ",email";
    //                + "birthday,email,first_name,gender,languages,last_name,locale,ads_management";
    FacebookClient facebookClient;
    User userFB;

    @FXML
    public void initialize() {
//        doAutorizatrion();
        authorization();
    }

    private void authorization() {
        webView.getEngine().load(authUrl);
        webView.getEngine().getLoadWorker().stateProperty().addListener(
                new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == Worker.State.SUCCEEDED) {
                            screensManager.getStage().setTitle(webView.getEngine().getLocation());
                            if (!webView.getEngine().getLocation().contains("facebook.com")) {
                                String url = webView.getEngine().getLocation();
                                accessToken = url.replaceAll(".*#access_token=(.+)&.*", "$1");
                                facebookClient = new DefaultFacebookClient(accessToken);
                                userFB = facebookClient.fetchObject("me", User.class);
                                System.out.println(userFB.getAbout() + " " + userFB.getEmail());
                            }
                        }
                    }
                });
    }


    public void login(String endpoint, String uri) throws AuthorizationException, URISyntaxException {
        final URI authorizationEndpoint = new URI(endpoint);
        final URI redirectUri = new URI(uri);

        final UserAgent userAgent = new UserAgentImpl();

        final AuthorizationResponse authorizationResponse = userAgent.requestAuthorizationCode(authorizationEndpoint, redirectUri);
        final String code = authorizationResponse.getCode();

        System.out.print("Authorization Code: ");
        System.out.println(code);
    }

    private void doAutorizatrion() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver webDriver = new ChromeDriver();
        webDriver.get(authUrl);
        while (true) { //Sprawdzamy czy uzytkownik jest na stronie facebooka
            if (webDriver.getCurrentUrl() != null) {
                if (!webDriver.getCurrentUrl().contains("facebook.com")) {
                    String url = webDriver.getCurrentUrl();
                    accessToken = url.replaceAll(".*#access_token=(.+)&.*", "$1");
                    webDriver.quit();
                    FacebookClient facebookClient = new DefaultFacebookClient(accessToken);
                    User userFB = facebookClient.fetchObject("me", User.class);
//                    messageLabel.setText(userFB.getFirstName() + "" + userFB.getLastName());
                    System.out.println(userFB.getAbout() + " " + userFB.getEmail());
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
