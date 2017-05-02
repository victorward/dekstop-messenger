package zaawjava.services;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaawjava.model.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final DatabaseConnector databaseConnector;

    @Autowired
    public UserService(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    private HashMap<Integer, UserChannelPair> listOfLoggedUsers = new HashMap<>();

    public void addUserToLoggedList(User user, Channel channel) {
        channel.closeFuture().addListener(future -> {
            deleteUserFromLoggedList(user);
        });
        listOfLoggedUsers.put(user.getId(), new UserChannelPair(user, channel));
    }

    public void deleteUserFromLoggedList(User user) {
        listOfLoggedUsers.remove(user.getId());
    }

    public int getNumberOfLoggedUsers() {
        return listOfLoggedUsers.size();
    }

    public boolean checkIfLogged(User user) {
        return listOfLoggedUsers.containsKey(user.getId());
    }

    public void printUserList() {
        for (Map.Entry<Integer, UserChannelPair> entry : listOfLoggedUsers.entrySet()) {
            System.out.println("|User list: " + entry.getKey() + " : " + entry.getValue().getUser());
        }
    }

    private class UserChannelPair {
        private User user;
        private Channel channel;

        public UserChannelPair(User user, Channel channel) {
            this.user = user;
            this.channel = channel;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public Channel getChannel() {
            return channel;
        }

        public void setChannel(Channel channel) {
            this.channel = channel;
        }
    }
}
