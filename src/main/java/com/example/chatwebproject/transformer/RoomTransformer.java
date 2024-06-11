package com.example.chatwebproject.transformer;

import com.example.chatwebproject.dto.RoomProjection;
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.enums.RoomType;

import java.util.List;
import java.util.stream.Collectors;

public class RoomTransformer {
    public static RoomDto toDto(Room room) {
        RoomDto roomDto = new RoomDto();
        roomDto.setId(room.getId());
        roomDto.setRoomType(room.getRoomType());
        roomDto.setName(room.getName());

        List<String> usernames = room.getUsers().stream()
                .map(User::getUsername)
                .collect(Collectors.toList());
        roomDto.setUsernames(usernames);
        return roomDto;
    }

    public static RoomDto toDtoFromProjection(RoomProjection roomProjection) {
        RoomDto roomDto = new RoomDto();
        roomDto.setId(roomProjection.getId());
        roomDto.setRoomType(RoomType.fromString(roomProjection.getConversationType()));
        roomDto.setName(roomProjection.getName());
        roomDto.setLastMessageContent(roomProjection.getLastMessageContent());
        roomDto.setLastMessageTime(roomProjection.getLastMessageTime());
        return roomDto;
    }
}
