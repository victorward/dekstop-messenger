package zaawjava.controllers;

import com.microsoft.alm.oauth2.useragent.AuthorizationException;
import com.microsoft.alm.oauth2.useragent.AuthorizationResponse;
import com.microsoft.alm.oauth2.useragent.UserAgent;
import com.microsoft.alm.oauth2.useragent.UserAgentImpl;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import zaawjava.ScreensManager;
import zaawjava.services.SocketService;
import zaawjava.services.UserService;

import java.net.URI;
import java.net.URISyntaxException;

public class LoginOAuth {
    private final SocketService socketService;
    private ScreensManager screensManager;
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public LoginOAuth(SocketService socketService) {
        this.socketService = socketService;
    }

    @Autowired
    public void setScreensManager(ScreensManager screensManager) {
        this.screensManager = screensManager;
    }

    @FXML
    public void initialize() {
        try {
            login("1", "2");
        } catch (AuthorizationException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
}
