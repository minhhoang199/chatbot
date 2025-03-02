package com.example.chatwebproject.transformer;

import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.RoomProjection;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.enums.RoomType;

import java.util.List;
import java.util.stream.Collectors;

public class RoomTransformer {
    public static RoomDto toDto(Room room) {
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomType(room.getRoomType());
        dto.setName(room.getName());

        List<String> emails = room.getUsers().stream()
                .map(User::getEmail)
                .collect(Collectors.toList());
        dto.setEmails(emails);
        dto.setLastMessageTime(room.getLastMessageTime());
        dto.setAdmin(room.getAdmin());
        dto.setPrivateKey(room.getPrivateKey());
        dto.setLastMessageTime(room.getLastMessageTime());
        return dto;
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
