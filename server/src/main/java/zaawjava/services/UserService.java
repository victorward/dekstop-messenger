package zaawjava.services;

import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaawjava.model.User;

import java.util.*;

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
        if (user != null) {
            return listOfLoggedUsers.containsKey(user.getId());
        }
        return true;
    }

    public void printUserList() {
        for (Map.Entry<Integer, UserChannelPair> entry : listOfLoggedUsers.entrySet()) {
            System.out.println("|User list: " + entry.getKey() + " : " + entry.getValue().getUser());
        }
    }

    public List<User> getListOfLoggedUsers() {
        List<User> userList = new ArrayList<User>();
        for (Map.Entry<Integer, UserChannelPair> entry : listOfLoggedUsers.entrySet()) {
            userList.add(entry.getValue().getUser());
        }
        return userList;
    }

    public Channel getUserChannel(User user) {
        if (listOfLoggedUsers.containsKey(user.getId())) {
            return listOfLoggedUsers.get(user.getId()).getChannel();
        }
        return null;
    }

    public User getUserByChannel(Channel channel) {
        Optional<UserChannelPair> optional = listOfLoggedUsers
                .values()
                .stream()
                .filter((entry) -> channel.equals(entry.getChannel()))
                .findFirst();

        return optional.map(UserChannelPair::getUser).orElse(null);
    }

//    public List<UserDTO> getListOfLoggedUsers() {
//        List<UserDTO> userList = new ArrayList<UserDTO>();
//        for (Map.Entry<Integer, UserChannelPair> entry : listOfLoggedUsers.entrySet()) {
//            userList.add(UtilsDTO.convertUserToDTO(entry.getValue().getUser()));
//        }
//        return userList;
//    }

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
