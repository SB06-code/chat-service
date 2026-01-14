package codeit.sb06.ws.service;

import codeit.sb06.ws.model.ChatMessage;
import codeit.sb06.ws.model.ChatRoom;
import codeit.sb06.ws.model.ChatUser;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConcurrentHashMap<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionToRoom = new ConcurrentHashMap<>();

    private final SimpMessageSendingOperations messagingTemplate;

    public void addUserToRoom(String roomId, String username, String sessionId) {
        ChatRoom room = chatRooms.computeIfAbsent(roomId, k -> new ChatRoom(roomId));
        ChatUser user = new ChatUser(username, sessionId);

        room.addUser(user);
        sessionToRoom.put(sessionId, roomId);

        ChatMessage joinMessage = new ChatMessage(
                ChatMessage.MessageType.JOIN,
                username + "님이 채팅방에 입장했습니다.",
                "시스템",
                roomId
        );
        sendMessageToRoom(roomId, joinMessage);
        sendUserListToRoom(roomId);
    }

    public void removeUserFromRoom(String sessionId) {
        String roomId = sessionToRoom.remove(sessionId);
        if (roomId != null) {
            ChatRoom room = chatRooms.get(roomId);
            if (room != null) {
                ChatUser removedUser = room.removeUser(sessionId);
                if (removedUser != null) {
                    ChatMessage leaveMessage = new ChatMessage(
                            ChatMessage.MessageType.LEAVE,
                            removedUser.getUsername() + "님이 채팅방을 나갔습니다.",
                            "시스템",
                            roomId
                    );
                    sendMessageToRoom(roomId, leaveMessage);
                    sendUserListToRoom(roomId);

                    if (room.isEmpty()) {
                        chatRooms.remove(roomId);
                    }
                }
            }
        }
    }

    public void sendMessageToRoom(String roomId, ChatMessage message) {
        if (chatRooms.containsKey(roomId)) {
            String destination = "/topic/chat/" + roomId;
            messagingTemplate.convertAndSend(destination, message);
        }
    }


    public String getUserRoom(String sessionId) {
        return sessionToRoom.get(sessionId);
    }

    private void sendUserListToRoom(String roomId) {
        ChatRoom room = chatRooms.get(roomId);
        if (room != null) {
            try {
                messagingTemplate.convertAndSend("/topic/chat/" + roomId + "/users", room.getUsers());
            } catch (Exception e) {
                System.err.println("사용자 목록 전송 실패: " + e.getMessage());
            }
        }
    }
}
