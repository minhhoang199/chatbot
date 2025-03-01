package com.example.chatwebproject.transformer;

import com.example.chatwebproject.dto.RoomProjection;
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.dto.SaveRoomRequest;
import com.example.chatwebproject.model.enums.RoomType;

import java.util.List;
import java.util.stream.Collectors;

public class RoomTransformer {
    public static SaveRoomRequest toDto(Room room) {
        SaveRoomRequest saveRoomRequest = new SaveRoomRequest();
        saveRoomRequest.setId(room.getId());
        saveRoomRequest.setRoomType(room.getRoomType());
        saveRoomRequest.setName(room.getName());

        List<String> usernames = room.getUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        saveRoomRequest.setPhones(usernames);
        return saveRoomRequest;
    }

    public static SaveRoomRequest toDtoFromProjection(RoomProjection roomProjection) {
        SaveRoomRequest saveRoomRequest = new SaveRoomRequest();
        saveRoomRequest.setId(roomProjection.getId());
        saveRoomRequest.setRoomType(RoomType.fromString(roomProjection.getConversationType()));
        saveRoomRequest.setName(roomProjection.getName());
        saveRoomRequest.setLastMessageContent(roomProjection.getLastMessageContent());
        saveRoomRequest.setLastMessageTime(roomProjection.getLastMessageTime());
        return saveRoomRequest;
    }
}
