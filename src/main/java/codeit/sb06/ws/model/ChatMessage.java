package codeit.sb06.ws.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    public enum MessageType {
        CHAT,    // 일반 채팅 메시지
        JOIN,    // 사용자 입장
        LEAVE    // 사용자 퇴장
    }

    private MessageType type;
    private String content;
    private String sender;
    private String roomId;
    private long timestamp;

    public ChatMessage(MessageType type, String content, String sender, String roomId) {
        this.type = type;
        this.content = content;
        this.sender = sender;
        this.roomId = roomId;
        this.timestamp = System.currentTimeMillis();
    }
}