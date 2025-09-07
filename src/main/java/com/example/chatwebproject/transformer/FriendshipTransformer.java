package com.example.chatwebproject.transformer;


import com.example.chatwebproject.model.dto.FriendshipDto;
import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.model.entity.ERole;
import com.example.chatwebproject.model.entity.Friendship;
import com.example.chatwebproject.model.entity.Role;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.request.SignupRequest;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

public class FriendshipTransformer {
    public static FriendshipDto toDto(Friendship entity){
        if (ObjectUtils.isEmpty(entity)) return null;
        FriendshipDto dto = new FriendshipDto();
        dto.setId(entity.getId());
        dto.setStatus(entity.getStatus());
        dto.setAcceptedUserEmail(entity.getAcceptedUserEmail());
        dto.setRequestUserEmail(entity.getRequestUserEmail());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setBlockUserEmail(entity.getBlockUserEmail());
        return dto;
    }

    public static List<FriendshipDto> toDtoList(List<Friendship> users) {
        return users.stream().map(FriendshipTransformer::toDto).collect(Collectors.toList());
    }
}
