package com.example.chatwebproject.aop.validation;

import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.model.entity.Room;
import com.example.chatwebproject.model.enums.RoomType;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
@RequiredArgsConstructor
public class ValidationRequestAspect {
    private final RoomRepository roomRepository;
    @Before("@annotation(ValidationRequest)")
    public void validateMethod(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        SaveRoomRequest request = (SaveRoomRequest)args[0];
        if (RoomType.PRIVATE_CHAT.equals(request.getRoomType())) {
            if (request.getEmails().size() != 2) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Private chat only have 2 users"});
            }
            // Reverse using Streams
            List<String> reversedListEmail = IntStream.range(0, request.getEmails().size())
                    .mapToObj(i -> request.getEmails().get(request.getEmails().size() - 1 - i))
                    .collect(Collectors.toList());
            List<String> privateKeys = List.of(String.join("-", request.getEmails()), String.join("-", reversedListEmail));

            List<Room> roomList = this.roomRepository.findByPrivateKeyIn(privateKeys);
            if (!CollectionUtils.isEmpty(roomList)) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Private chat of 2 users already exists"});
            }
        } else {
            if (request.getEmails().size() < 3) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Group chat must have at least 3 users"});
            }
        }
    }
}
