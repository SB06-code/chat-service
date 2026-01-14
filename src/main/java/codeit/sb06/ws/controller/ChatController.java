package codeit.sb06.ws.controller;

import codeit.sb06.ws.service.ChatService;
import codeit.sb06.ws.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat/{roomId}/join")
    public void joinRoom(@DestinationVariable String roomId,
                         @Payload ChatMessage joinMessage,
                         SimpMessageHeaderAccessor headerAccessor) {

        String sessionId = headerAccessor.getSessionId();
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        if (username == null || username.trim().isEmpty()) {
            return;
        }

        try {
            chatService.addUserToRoom(roomId, username.trim(), sessionId);
        } catch (RuntimeException e) {
            System.err.println("채팅방 입장 실패: " + e.getMessage());
        }
    }

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable String roomId,
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        String sessionId = headerAccessor.getSessionId();
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String userRoom = chatService.getUserRoom(sessionId);

        // 보안 검증
        if (!roomId.equals(userRoom)) {
            System.err.println("잘못된 채팅방 접근: " + username + " -> " + roomId);
            return;
        }

        // 메시지 검증
        if (chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
            return;
        }

        if (chatMessage.getContent().length() > 500) {
            return;
        }

        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setSender(username);
        chatMessage.setRoomId(roomId);
        chatMessage.setTimestamp(System.currentTimeMillis());

        chatService.sendMessageToRoom(roomId, chatMessage);
    }
}
