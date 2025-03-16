package com.example.chatwebproject.service;

//22/06: Update create room chat addNewRoom() method

import com.example.chatwebproject.aop.validation.ValidationRequest;
import com.example.chatwebproject.constant.Constants;
import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.model.entity.Connection;
import com.example.chatwebproject.model.entity.Message;
import com.example.chatwebproject.model.entity.Room;
import com.example.chatwebproject.model.entity.RoomProjection;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.dto.MessageDto;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.enums.*;
import com.example.chatwebproject.model.request.GetListRoomRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.dto.InviteeDto;
import com.example.chatwebproject.repository.ConnectionRepository;
import com.example.chatwebproject.repository.MessageRepository;
import com.example.chatwebproject.repository.RoomRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.transformer.RoomTransformer;
import com.example.chatwebproject.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final MessageRepository messageRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final UserService userService;
    private final MessageService messageService;

    private boolean validatePhone(String phone) {
        Pattern pattern = Pattern.compile("^0\\d{9}$|^84\\d{9}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.find();
    }

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
            User currentUser = this.userService.getUserInfo(email);
            currentUser.getRooms().add(newRoom);
            users.add(currentUser);
        }
        newRoom.setPrivateKey(String.join("-", request.getEmails()));
    }

    private void createGroupRoom(SaveRoomRequest request, Room newRoom, Set<User> users, List<Message> messages){
        for (String email : request.getEmails()) {
            User currentUser = userService.getUserInfo(email);
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
        newRoom.setAdmin(SecurityUtil.getCurrentEmailLogin());
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

        User invitor = this.userService.getUserInfo(invitorEmail);

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

            User currentUser = this.userService.getUserInfo(email);

            currentUser.getRooms().add(currentRoom);
            this.userRepository.save(currentUser);
            users.add(currentUser);
        }
        currentRoom.setUsers(users);
        this.roomRepository.save(currentRoom);
    }

    //user block/gỡ block private chat
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

    public RoomDto findRoomById(Long roomId) {
        Room room = this.roomRepository.findByIdAndDelFlag(roomId).orElseThrow(
                () -> new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Not found room by id: " + roomId})
        );

        return RoomTransformer.toDto(room);
    }

    @Transactional
    public void outRoom(Long roomId){
        RoomDto roomDto = this.findRoomById(roomId);
        //check roomDto is group chat or not
        if (!roomDto.getRoomType().equals(RoomType.GROUP_CHAT)) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Room is not group chat: " + roomId});
        }

        //check user in group chat or not
        String email = SecurityUtil.getCurrentEmailLogin();
        if (CollectionUtils.isEmpty(roomDto.getEmails()) || !roomDto.getEmails().contains(email)) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"This user is not in group chat: " + roomId});
        }

        Room roomEntity = this.entityManager.find(Room.class, roomId);
        User currentUser = User.builder().email(email).build();
        for (User user:roomEntity.getUsers()) {
            if (user.getEmail().equals(email)) {
                this.messageService.saveMessage(MessageDto.builder()
                        .sender(email)
                        .roomId(roomId)
                        .type(MessageType.LEAVE)
                        .build());
                user.getRooms().remove(roomEntity);
                currentUser = user;
            }
        }
        roomEntity.getUsers().remove(currentUser);
        //set new admin and delete room if there is not user remain in room
        User nextUser = roomEntity.getUsers().stream().findFirst().orElse(null);
        roomEntity.setAdmin(ObjectUtils.isEmpty(nextUser) ? null : nextUser.getEmail());
        roomEntity.setDelFlag(ObjectUtils.isEmpty(nextUser));
        this.roomRepository.save(roomEntity);
    }

    @Transactional
    public void addUserToRoom(List<String> emails, Long roomId){
        RoomDto roomDto = this.findRoomById(roomId);
        //check roomDto is group chat or not
        if (!roomDto.getRoomType().equals(RoomType.GROUP_CHAT)) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Room is not group chat: " + roomId});
        }

        //check user in group chat or not
        String currentEmailLogin = SecurityUtil.getCurrentEmailLogin();
        if (CollectionUtils.isEmpty(roomDto.getEmails()) || !roomDto.getEmails().contains(currentEmailLogin)) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"This user is not in group chat: " + roomId});
        }

        //add users to group
        Room roomEntity = this.entityManager.find(Room.class, roomId);
        for (String email:emails) {
            User user = this.userService.getUserInfo(email);
            this.messageService.saveMessage(MessageDto.builder()
                    .sender(email)
                    .roomId(roomId)
                    .type(MessageType.JOIN)
                    .build());
            user.getRooms().add(roomEntity);
            roomEntity.getUsers().add(user);
        }
        this.roomRepository.save(roomEntity);
        this.userRepository.saveAll(roomEntity.getUsers());
    }

    @Transactional
    public void removeUsersToRoom(List<String> emails, Long roomId){
        RoomDto roomDto = this.findRoomById(roomId);
        //check roomDto is group chat or not
        if (!roomDto.getRoomType().equals(RoomType.GROUP_CHAT)) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"Room is not group chat: " + roomId});
        }

        //check user in group chat or not
        String currentEmailLogin = SecurityUtil.getCurrentEmailLogin();
        if (!currentEmailLogin.equals(roomDto.getAdmin())) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"This user is not admin in group chat: " + roomId});
        }

        //remove users to group
        Room roomEntity = this.entityManager.find(Room.class, roomId);
        for (String email:emails) {
            User user = this.userService.getUserInfo(email);
            user.getRooms().remove(roomEntity);
            roomEntity.getUsers().remove(user);
        }
        this.messageService.saveMessage(MessageDto.builder()
                        .sender(currentEmailLogin)
                //.content(currentEmailLogin + " has remove " + String.join(" ,", emails))
                .content(String.join(" ,", emails))
                .roomId(roomId)
                .type(MessageType.EDITED)
                .build());
        this.roomRepository.save(roomEntity);
    }

    //TODO: add change name message
    @Transactional
    public void changeRoomName(Long roomId, String newName){
        if (StringUtils.isBlank(newName)) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"New name can not be blank"});
        }
        RoomDto roomDto = this.findRoomById(roomId);
        //check user in group chat or not
        String currentEmailLogin = SecurityUtil.getCurrentEmailLogin();
        if (CollectionUtils.isEmpty(roomDto.getEmails()) || !roomDto.getEmails().contains(currentEmailLogin)) {
            throw new ChatApplicationException(DomainCode.INVALID_PARAMETER, new Object[]{"This user is not in group chat: " + roomId});
        }
        //change room name
        Room roomEntity = this.entityManager.find(Room.class, roomId);
        roomEntity.setName(newName);
        this.messageService.saveMessage(MessageDto.builder()
                        .content(currentEmailLogin + " changes group name to " + newName)
                        .sender(currentEmailLogin)
                        .roomId(roomId)
                        .type(MessageType.EDITED)
                .build());
        this.roomRepository.save(roomEntity);
    }
}