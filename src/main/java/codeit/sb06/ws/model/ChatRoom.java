package codeit.sb06.ws.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {
    private String roomId;
    private String roomName;
    private ConcurrentHashMap<String, ChatUser> users;
    private long createdTime;

    public ChatRoom(String roomId) {
        this.roomId = roomId;
        this.roomName = roomId;
        this.users = new ConcurrentHashMap<>();
        this.createdTime = System.currentTimeMillis();
    }

    public void addUser(ChatUser user) {
        users.put(user.getSessionId(), user);
    }

    public ChatUser removeUser(String sessionId) {
        return users.remove(sessionId);
    }

    public int getUserCount() {
        return users.size();
    }

    public boolean isEmpty() {
        return users.isEmpty();
    }
}