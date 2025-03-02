package com.example.chatwebproject.service;

//22/06: Update create room chat addNewRoom() method

import com.example.chatwebproject.aop.validation.ValidationRequest;
import com.example.chatwebproject.constant.Constants;
import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.model.Connection;
import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.RoomProjection;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.enums.*;
import com.example.chatwebproject.model.request.GetListRoomRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.model.response.AddRoomResponse;
import com.example.chatwebproject.repository.ConnectionRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.repository.RoomRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.transformer.RoomTransformer;
import com.example.chatwebproject.utils.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
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

    private boolean validatePhone(String phone) {
        Pattern pattern = Pattern.compile("^0\\d{9}$|^84\\d{9}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.find();
    }
    //TODO: user thoát/join room
    //TODO: user admin kick/add user khác
    //TODO: user đổi tên nhóm
    //TODO: user block/gỡ block private chat

    //TODO: Khi tìm kiếm user khác và click vào user đó, tự động tạo 1 Private chat -> API addRoom chỉ để tạo Group_chat
    @Transactional
    @ValidationRequest
    public RoomDto addNewRoom(SaveRoomRequest request) {
        //validate
        Room newRoom = new Room();
        newRoom.setName(request.getName());
        newRoom.setRoomType(request.getRoomType());

        Set<User> users = new HashSet<>();
        List<Message> messages = new ArrayList<>();
        //add users to the list
        if (RoomType.GROUP_CHAT.equals(request.getRoomType())) {
            this.createGroupRoom(request, newRoom, users, messages);
        } else this.createPrivateRoom(request, newRoom, users);
        newRoom.setUsers(users);
        newRoom.setRoomStatus(RoomStatus.ENABLE);
        newRoom.getMessages().addAll(messages);

        //Save to database
        this.userRepository.saveAll(users);
        Room roomEntity = this.roomRepository.save(newRoom);
        this.messageRepository.saveAll(messages);
        return RoomTransformer.toDto(roomEntity);
    }

    private void createPrivateRoom(SaveRoomRequest request, Room newRoom, Set<User> users){
        StringBuilder sb = new StringBuilder(Constants.EMPTY_STRING);
        for (String email : request.getEmails()) {
            Optional<User> optionalUser = this.userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found user by email " + email});
            }

            User currentUser = optionalUser.get();
            currentUser.getRooms().add(newRoom);
            users.add(currentUser);
        }
        newRoom.setPrivateKey(String.join("-", request.getEmails()));
    }

    private void createGroupRoom(SaveRoomRequest request, Room newRoom, Set<User> users, List<Message> messages){
        for (String email : request.getEmails()) {
            Optional<User> optionalUser = this.userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found user by email " + email});
            }

            User currentUser = optionalUser.get();
            //Save join messages for group
            Message newMessage = new Message();
            newMessage.setMessageStatus(MessageStatus.ACTIVE);
            newMessage.setType(MessageType.JOIN);
            newMessage.setRoom(newRoom);
            newMessage.setContent(currentUser.getUsername() + "have been added to the chat room");
            messages.add(newMessage);

            currentUser.getRooms().add(newRoom);
            users.add(currentUser);
        }
        newRoom.setAdmin(SecurityUtils.getCurrentEmailLogin());
    }

    public List<RoomDto> getAllByUserId(GetListRoomRequest request) {
        List<RoomProjection> rooms = roomRepository.findByUserId2(request.getUserId());
        if (!CollectionUtils.isEmpty(rooms)) {
            return rooms.stream()
                    .map(RoomTransformer::toDtoFromProjection)
                    .collect(Collectors.toList());
        }

        log.error("RoomService :: getAll : Not found any room with userId " + request.getUserId());
        return null;
    }

    public void addMoreUser(InviteeDto inviteeDto, Long conversationId) {
        String invitorEmail = inviteeDto.getInvitorEmail();

        Optional<User> optionalInvitor = this.userRepository.findByEmail(invitorEmail);
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

        for (String email : inviteeDto.getInviteeEmails()) {
            Optional<User> optionalUser = this.userRepository.findByEmail(email);
            if (optionalUser.isEmpty()) {
                throw new RuntimeException("Not found account by email " + email);
            }
            Optional<Connection> optionalConnectionWithUser = this.connectionRepository.findByUsersAndStatus(
                    invitorEmail,
                    email,
                    ConnectionStatus.CONNECTED);
            Optional<Connection> optionalConnectionWithInvitor = this.connectionRepository.findByUsersAndStatus(
                    email,
                    invitorEmail,
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