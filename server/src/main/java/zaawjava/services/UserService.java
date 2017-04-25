package zaawjava.services;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserService {
    private final DatabaseConnector databaseConnector;

    @Autowired
    public UserService(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public DatabaseConnector getDatabaseConnector() {
        return databaseConnector;
    }

    private HashMap<Integer, User> listOfLogedUsers = new HashMap<>();

    public void addUserToLoggedList(User user) {
        listOfLogedUsers.put(user.getId(), user);
    }

    public void deleteUserFromLoggedList(User user) {
        listOfLogedUsers.remove(user.getId());
    }
}
