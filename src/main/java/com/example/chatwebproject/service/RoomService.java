package com.example.chatwebproject.service;

//22/06: Update create room chat addNewRoom() method

import com.example.chatwebproject.dto.RoomProjection;
import com.example.chatwebproject.dto.request.AddRoomRequest;
import com.example.chatwebproject.dto.request.GetListRoomRequest;
import com.example.chatwebproject.dto.response.AddRoomResponse;
import com.example.chatwebproject.dto.response.Result;
import com.example.chatwebproject.model.Connection;
import com.example.chatwebproject.model.Message;
import com.example.chatwebproject.model.Room;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.enums.*;
import com.example.chatwebproject.model.dto.SaveRoomRequest;
import com.example.chatwebproject.model.dto.InviteeDto;
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
    public AddRoomResponse addNewRoom(AddRoomRequest request) {
        AddRoomResponse response = new AddRoomResponse();
        Result result;
        SaveRoomRequest saveRoomRequest = request.getSaveRoomRequest();
        //validate
        Room newRoom = new Room();
        newRoom.setName(saveRoomRequest.getName());
        newRoom.setRoomType(request.getIsPrivateChat() ? RoomType.PRIVATE_CHAT : RoomType.GROUP_CHAT);

        Set<User> users = new HashSet<>();
        List<Message> messages = new ArrayList<>();
        //add users to the list
        //TODO: tạo 2 method riêng cho private và group
        //TODO: private: check xem room chat giữa 2 người đã tồn tại chưa, số lượng user bắt buộc bằng 2
        //TODO: group: số lượng user bắt buộc phải lớn hơn hoặc bằng 3, tên room + admin (là user tạo)
        //TODO: nên đổi phone thành email
        if (!request.getIsPrivateChat()) {
            for (String phone : saveRoomRequest.getPhones()
            ) {
                if (!validatePhone(phone)) {
                    result = new Result("400", "Invalid phone format: " + phone, null);
                    response.setResult(result);
                    return response;
                }

                Optional<User> optionalUser = this.userRepository.findByPhone(phone);
                if (optionalUser.isEmpty()) {
                    result = new Result("404", "Not found user by phone: " + phone, null);
                    response.setResult(result);
                    return response;
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
        }
        newRoom.setUsers(users);
        newRoom.setRoomStatus(RoomStatus.ENABLE);
        newRoom.getMessages().addAll(messages);

        //Save to database
        this.userRepository.saveAll(users);
        this.roomRepository.save(newRoom);
        this.messageRepository.saveAll(messages);

        result = new Result("200", "Create room succeed", null);
        response.setResult(result);
        return response;
    }

    public List<SaveRoomRequest> getAllByUserId(GetListRoomRequest request) {
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