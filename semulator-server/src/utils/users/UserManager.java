package utils.users;

import dto.UserInfo;

import java.util.*;

public class UserManager {

    private final Map<String, UserInfo> users;

    public UserManager() {
        users = new HashMap<>();
    }

    public synchronized void addUser(String username) {
        users.put(username, new UserInfo(username));
    }

    public synchronized void removeUser(String username) {
        users.remove(username);
    }

    public synchronized Map<String,UserInfo> getUsers() {
        return Collections.unmodifiableMap(users);
    }

    public boolean isUserExists(String username) {
        return users.containsKey(username);
    }
}

