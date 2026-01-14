package codeit.sb06.ws.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUser {
    private String username;
    private String sessionId;
    private long joinTime;

    public ChatUser(String username, String sessionId) {
        this.username = username;
        this.sessionId = sessionId;
        this.joinTime = System.currentTimeMillis();
    }
}