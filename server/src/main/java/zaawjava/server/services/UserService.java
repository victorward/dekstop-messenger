package zaawjava.server.services;

import io.netty.channel.Channel;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zaawjava.commons.DTO.UserDTO;

import java.util.*;

@Service
public class UserService {
    private DefaultChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private HashMap<Integer, UserChannelPair> listOfLoggedUsers = new HashMap<>();
    private DatabaseConnector databaseConnector;

    @Autowired
    public void setDatabaseConnector(DatabaseConnector databaseConnector) {
        this.databaseConnector = databaseConnector;
    }

    public void addUserToLoggedList(UserDTO user, Channel channel) {
        if (listOfLoggedUsers.containsKey(user.getId())) {
            throw new IllegalArgumentException("User already logged");
        }
        allChannels.add(channel);
        channel.closeFuture().addListener(future -> {
            deleteUserFromLoggedList(user);
        });
        listOfLoggedUsers.put(user.getId(), new UserChannelPair(user, channel));
    }

    public void deleteUserFromLoggedList(UserDTO user) {
        listOfLoggedUsers.remove(user.getId());
    }

    public void deleteUserFromLoggedList(Channel channel) {
        listOfLoggedUsers.values().removeIf((val) -> val.getChannel().equals(channel));
    }

    public int getNumberOfLoggedUsers() {
        return listOfLoggedUsers.size();
    }

    public boolean checkIfLogged(UserDTO user) {
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

    public List<UserDTO> getListOfLoggedUsers() {
        List<UserDTO> userList = new ArrayList<>();
        for (Map.Entry<Integer, UserChannelPair> entry : listOfLoggedUsers.entrySet()) {
            userList.add(entry.getValue().getUser());
        }
        return userList;
    }

    public Channel getUserChannel(UserDTO user) {
        if (listOfLoggedUsers.containsKey(user.getId())) {
            return listOfLoggedUsers.get(user.getId()).getChannel();
        }
        return null;
    }

    public UserDTO getUserByChannel(Channel channel) {
        Optional<UserChannelPair> optional = listOfLoggedUsers
                .values()
                .stream()
                .filter((entry) -> channel.equals(entry.getChannel()))
                .findFirst();

        return optional.map(UserChannelPair::getUser).orElse(null);
    }

    public HashMap<UserDTO, Boolean> getMapOfUsersWithStatus() {
        HashMap<UserDTO, Boolean> userList = new HashMap<>();
        List<UserDTO> activeUsers = getListOfLoggedUsers();
        List<UserDTO> allUsers = databaseConnector.getAllUsers();
        for (UserDTO user : allUsers) {
            Boolean status = false;
            for (UserDTO userAct : activeUsers) {
                if (userAct.getId() == user.getId()) status = true;
            }
            userList.put(user, status);
        }
        return userList;
    }

    public DefaultChannelGroup getAllChannels() {
        return allChannels;
    }
    //    public List<UserDTO> getListOfLoggedUsers() {
//        List<UserDTO> userList = new ArrayList<UserDTO>();
//        for (Map.Entry<Integer, UserChannelPair> entry : listOfLoggedUsers.entrySet()) {
//            userList.add(DTOUtils.convertUserToDTO(entry.getValue().getUser()));
//        }
//        return userList;
//    }

    private class UserChannelPair {
        private UserDTO user;
        private Channel channel;

        public UserChannelPair(UserDTO user, Channel channel) {
            this.user = user;
            this.channel = channel;
        }

        public UserDTO getUser() {
            return user;
        }

        public void setUser(UserDTO user) {
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
