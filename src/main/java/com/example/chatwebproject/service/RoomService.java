package com.example.chatwebproject.service;

//22/06: Update create room chat addNewRoom() method
import com.example.chatwebproject.model.Connection;
import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.enums.*;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.model.dto.PrivateConversationDto;
import com.example.chatwebproject.repository.ConnectionRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.repository.RoomRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.transformer.RoomTransformer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RoomService {
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private ConnectionRepository connectionRepository;
    private MessageRepository messageRepository;

    private void validatePhone(String phone) {
        Pattern pattern = Pattern.compile("^0\\d{9}$|^84\\d{9}$");
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.find()) {
            throw new RuntimeException(phone + ": Invalid phone format");
        }
    }


    public void addNewPrivateConversation(PrivateConversationDto privateConversationVM) {
        if (privateConversationVM != null) {
            //validation
            String name = privateConversationVM.getName();
//            if (name == null ||
//                    name.length() <= 1) {
//                throw new RuntimeException("Invalid name");
//            }

            String invitorPhone = privateConversationVM.getInvitorPhone();
//            validatePhone(invitorPhone);
            Optional<User> optionalInvitor = this.userRepository.findByPhone(invitorPhone);
            if (optionalInvitor.isEmpty()) {
                throw new RuntimeException("Not found invitor");
            }
            User invitor = optionalInvitor.get();

            String inviteePhone = privateConversationVM.getInviteePhone();
//            validatePhone(inviteePhone);
            Optional<User> optionalInvitee = this.userRepository.findByPhone(inviteePhone);
            if (optionalInvitee.isEmpty()) {
                throw new RuntimeException("Not found invitee");
            }
            User invitee = optionalInvitee.get();

            List<Room> roomList = this.roomRepository.findByPhoneAndType(invitorPhone, RoomType.PRIVATE_CHAT);
            for (Room room : roomList){
                if (room.getUsers().contains(invitee)){
                    throw new RuntimeException("Private chat already existed");
                }
            }

            Room newRoom = new Room();
            newRoom.setName(name);
            newRoom.setRoomType(RoomType.PRIVATE_CHAT);

            //add invitor to the list
            Set<User> users = new HashSet<>();

            //add users to the list
            Optional<Connection> optionalConnectionWithInvitee = this.connectionRepository.findByUsersAndStatus(
                    invitorPhone,
                    inviteePhone,
                    ConnectionStatus.CONNECTED);
            Optional<Connection> optionalConnectionWithInvitor = this.connectionRepository.findByUsersAndStatus(
                    inviteePhone,
                    invitorPhone,
                    ConnectionStatus.CONNECTED);
            if (optionalConnectionWithInvitor.isEmpty() || optionalConnectionWithInvitee.isEmpty()) {
                throw new RuntimeException("Invalid connection between invitor and invitee");
            }

            invitor.getRooms().add(newRoom);
            //this.userRepository.save(invitor);
            users.add(invitor);

            invitee.getRooms().add(newRoom);
            //this.userRepository.save(invitee);
            users.add(invitee);

            newRoom.setUsers(users);
            newRoom.setRoomStatus(RoomStatus.ENABLE);
            this.userRepository.saveAll(users);
            this.roomRepository.save(newRoom);
        }
    }



    public void addNewRoom(RoomDto roomDto) {
        if (roomDto != null) {
            //validate
            Room newRoom = new Room();
            newRoom.setName(roomDto.getName());
            newRoom.setRoomType(roomDto.getRoomType());

            Set<User> users = new HashSet<>();
            List<Message> messages = new ArrayList<>();

            //add users to the list
            for (String phone : roomDto.getUsernames()
            ) {
                validatePhone(phone);

                Optional<User> optionalUser = this.userRepository.findByPhone(phone);
                if (optionalUser.isEmpty()) {
                    throw new RuntimeException("Not found account by phone");
                }

                User currentUser = optionalUser.get();
                //Save join messages for group
                if (roomDto.getRoomType().equals(RoomType.GROUP_CHAT)){
                    Message newMessage = new Message();
                    newMessage.setMessageStatus(MessageStatus.ACTIVE);
                    newMessage.setType(MessageType.JOIN);
                    newMessage.setRoom(newRoom);
                    newMessage.setContent(currentUser.getUsername() + "have been added to the chat room");
                    messages.add(newMessage);
                }

                currentUser.getRooms().add(newRoom);
                users.add(currentUser);
            }
            newRoom.setUsers(users);
            newRoom.setRoomStatus(RoomStatus.ENABLE);
            newRoom.getMessages().addAll(messages);

            //Save to database
            this.userRepository.saveAll(users);
            this.roomRepository.save(newRoom);
            this.messageRepository.saveAll(messages);
        }
    }

    public Set<RoomDto> getAllByUserId(Long userId) {
        List<Room> rooms = roomRepository.findByUserId(userId);
        if (!CollectionUtils.isEmpty(rooms)){
            return rooms.stream()
                    .map(RoomTransformer::toDto)
                    .collect(Collectors.toSet());
        }

        log.error("RoomService :: getAll : Not found any room with userId " + userId);
        return null;
    }

    public void addMoreUser(InviteeDto inviteeDto, Long conversationId) {
        String invitorPhone = inviteeDto.getInvitorPhone();
        validatePhone(invitorPhone);

        Optional<User> optionalInvitor = this.userRepository.findByPhone(invitorPhone);
        if (optionalInvitor.isEmpty()) {
            throw new RuntimeException("Not found invitor");
        }
        User invitor = optionalInvitor.get();

        var optConversation = roomRepository.findById(conversationId);
        if (optConversation.isEmpty()) {
            throw new RuntimeException("Not found conversation");
        } else if (optConversation.get().getRoomType() == RoomType.PRIVATE_CHAT) {
            throw new RuntimeException("Invalid conversation Type: private chat can not add more users");
        }
        Room currentRoom = optConversation.get();
        Set<User> users = currentRoom.getUsers();
        if (!users.contains(invitor)) {
            throw new RuntimeException("Invitor does not belong to conversation");
        }

        List<String> inviteePhones = inviteeDto.getInviteePhones();
        for (String phone : inviteePhones
        ) {
            validatePhone(phone);

            Optional<User> optionalUser = this.userRepository.findByPhone(phone);
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("Not found account by phone " + phone);
            }
            Optional<Connection> optionalConnectionWithUser = this.connectionRepository.findByUsersAndStatus(
                    invitorPhone,
                    phone,
                    ConnectionStatus.CONNECTED);
            Optional<Connection> optionalConnectionWithInvitor = this.connectionRepository.findByUsersAndStatus(
                    phone,
                    invitorPhone,
                    ConnectionStatus.CONNECTED);
            if (optionalConnectionWithInvitor.isEmpty() || optionalConnectionWithUser.isEmpty()) {
                throw new RuntimeException("Invalid connection between invitor and User");
            }

            User currentUser = optionalUser.get();

            currentUser.getRooms().add(currentRoom);
            this.userRepository.save(currentUser);
            users.add(currentUser);
        }
        currentRoom.setUsers(users);
        this.roomRepository.save(currentRoom);
    }

    public void changeConversationStatus(Long id, RoomStatus roomStatus) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Invalid Id");
        }

        Optional<Room> optionalConversation = this.roomRepository.findById(id);
        if (optionalConversation.isEmpty()) {
            throw new RuntimeException("Not found conversation");
        }

        Room currentRoom = optionalConversation.get();
        currentRoom.setRoomStatus(roomStatus);
        this.roomRepository.save(currentRoom);
    }
}