package zaawjava.services;

import model.User;
import org.springframework.stereotype.Service;

/**
 * @author Yuriy
 */
@Service
public class UserService {

    public User getUser() {
        return user;
    }

    private User user;
    private Integer userid;

    public UserService() {
    }

    public UserService(User user) {
        this.user = user;
//        this.userid = user.getId();
    }

    public void setUser(User user) {
        this.user = user;
        this.userid = user.getId();
    }
}
