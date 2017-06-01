package zaawjava.client.services;

import zaawjava.commons.DTO.UserDTO;
import org.springframework.stereotype.Service;

/**
 * @author Yuriy
 */
@Service
public class UserService {

    public UserDTO getUser() {
        return user;
    }

    private UserDTO user;
    private Integer userid;

    public UserService() {
    }

    public UserService(UserDTO user) {
        this.user = user;
//        this.userid = user.getId();
    }

    public void setUser(UserDTO user) {
        this.user = user;
        this.userid = user.getId();
    }
}
