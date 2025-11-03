package com.example.chatwebproject.service;


import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.dto.FriendshipDto;
import com.example.chatwebproject.model.dto.RoomDto;
import com.example.chatwebproject.model.dto.UserDto;
import com.example.chatwebproject.model.entity.Friendship;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.enums.RoomStatus;
import com.example.chatwebproject.model.request.ChangeFriendshipStatusRequest;
import com.example.chatwebproject.model.request.ChangeRoomStatusRequest;
import com.example.chatwebproject.model.request.CreateFriendshipRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.enums.FriendshipStatus;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.repository.FriendshipRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.transformer.FriendshipTransformer;
import com.example.chatwebproject.utils.Constant;
import com.example.chatwebproject.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;
    private final RespFactory respFactory;

    //TODO: API tạo request kết bạn (connection)
    //TODO: Tìm solution để FE nhận notification
    @Transactional
    public FriendshipDto createFriendship(CreateFriendshipRequest request) {
        String requestEmail = request.getRequestEmail();
        String acceptedEmail = request.getAcceptedEmail();
        ResponseEntity<BaseResponse> response;
        //check friend request already exist
        List<Friendship> friendshipList = this.friendshipRepository.findByUsersAndStatus(requestEmail,
                acceptedEmail,
                List.of(FriendshipStatus.PENDING, FriendshipStatus.ACCEPTED));
        if (!CollectionUtils.isEmpty(friendshipList)) {
            throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Friendship request already existed"}, null);
        }

        //check users already exist
        Optional<User> otpRequestUser = this.userRepository.findByEmailAndDelFlg(requestEmail);
        if (otpRequestUser.isEmpty()) {
            throw new ValidationRequestException(DomainCode.NOT_FOUND_DATA, new Object[]{"Not found request user: " + requestEmail}, null);
        }

        Optional<User> otpAcceptedUser = this.userRepository.findByEmailAndDelFlg(acceptedEmail);
        if (otpAcceptedUser.isEmpty()) {
            throw new ValidationRequestException(DomainCode.NOT_FOUND_DATA, new Object[]{"Not found accept user: " + acceptedEmail}, null);
        }

        Friendship newFriendship = new Friendship();
        newFriendship.setRequestUserEmail(requestEmail);
        newFriendship.setAcceptedUserEmail(acceptedEmail);
        newFriendship.setStatus(FriendshipStatus.PENDING);

        return FriendshipTransformer.toDto(friendshipRepository.save(newFriendship));
    }

    @Transactional
    public FriendshipDto changeStatus(ChangeFriendshipStatusRequest request) {
        Friendship friendship = this.friendshipRepository.findByIdAndDelFlag(request.getId()).orElseThrow(
                () -> new ValidationRequestException(DomainCode.NOT_FOUND_DATA, new Object[]{"Not found connection: " + request.getId()}, null));

        String currentEmail = SecurityUtil.getCurrentEmailLogin();

        //Accept
        if (request.getFriendshipStatus().equals(FriendshipStatus.ACCEPTED)) {
            //nếu status hiện tại là pending thì user thực hiện phải là acceptUser trong record friendship
            //nếu status hiện tại là block thì user thực hiện phải là blockedUser trong record friendship -> bỏ block về thành accept
            if ((friendship.getStatus().equals(FriendshipStatus.PENDING) && !StringUtils.equals(currentEmail, friendship.getAcceptedUserEmail()))
                    || (friendship.getStatus().equals(FriendshipStatus.BLOCKED) && !StringUtils.equals(currentEmail, friendship.getBlockUserEmail()))) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"This user can not accept this request friend: " + request.getId()}, null);
            } else if (!friendship.getStatus().equals(FriendshipStatus.PENDING) && !friendship.getStatus().equals(FriendshipStatus.BLOCKED)) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid status friendship: " + request.getFriendshipStatus()}, null);
            }
            if (friendship.getStatus().equals(FriendshipStatus.BLOCKED)) {
                this.changeRoomStatus(friendship.getAcceptedUserEmail().equals(currentEmail) ? friendship.getRequestUserEmail() : friendship.getAcceptedUserEmail(), RoomStatus.ENABLE);
            }
        }

        //Rejected : status hiện tại phải là pending
        else if (request.getFriendshipStatus().equals(FriendshipStatus.REJECTED)) {
            //nếu status hiện tại là pending thì user thực hiện phải là acceptUser trong record friendship
            if (!StringUtils.equals(currentEmail, friendship.getAcceptedUserEmail())) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"This user can not reject this request friend: " + request.getId()}, null);
            }
            if (!friendship.getStatus().equals(FriendshipStatus.PENDING)) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid status friendship: " + request.getFriendshipStatus()}, null);
            }
            friendship.setDelFlag(true);
        }

        //Block : status hiện tại phải là accpeted
        else if (request.getFriendshipStatus().equals(FriendshipStatus.BLOCKED)) {
            if (!friendship.getStatus().equals(FriendshipStatus.ACCEPTED)) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid status friendship: " + request.getFriendshipStatus()}, null);
            }
            friendship.setBlockUserEmail(currentEmail);
            this.changeRoomStatus(friendship.getAcceptedUserEmail().equals(currentEmail) ? friendship.getRequestUserEmail() : friendship.getAcceptedUserEmail(), RoomStatus.BLOCKED);
        }

        //Removed (user request tự xóa hoặc unfriend nhau) : status hiện tại phải là accpeted hoặc pending
        else if (request.getFriendshipStatus().equals(FriendshipStatus.REMOVED)) {
            //nếu status hiện tại là pending thì user thực hiện phải là requestUser trong record friendship
            //nếu status hiện tại là accept thì user thực hiện có thể là 1 trong 2
            if ((friendship.getStatus().equals(FriendshipStatus.PENDING) && !StringUtils.equals(currentEmail, friendship.getRequestUserEmail()))) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"This user can not remove this request friend: " + request.getId()}, null);
            }
            if (!friendship.getStatus().equals(FriendshipStatus.ACCEPTED) && !friendship.getStatus().equals(FriendshipStatus.PENDING)) {
                throw new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid status friendship: " + request.getFriendshipStatus()}, null);
            }
            friendship.setDelFlag(true);
        }

        //Update connection
        friendship.setStatus(request.getFriendshipStatus());
        friendship = this.friendshipRepository.save(friendship);
        return FriendshipTransformer.toDto(friendship);
    }

    private void changeRoomStatus(String otherEmail, RoomStatus status) {
        RoomDto roomDto = this.roomService.getRoomByEmail(otherEmail);
        this.roomService.changeStatus(ChangeRoomStatusRequest.builder()
                .id(roomDto.getId())
                .roomStatus(status)
                .build());
    }

    public List<FriendshipDto> getIncomingRequest() {
        String currentEmail = SecurityUtil.getCurrentEmailLogin();
        List<Friendship> friendshipList = this.friendshipRepository.getIncomingRequest(currentEmail);
        if (CollectionUtils.isEmpty(friendshipList)) return new ArrayList<>();
        return FriendshipTransformer.toDtoList(friendshipList);
    }

    public List<FriendshipDto> getOutgoingRequest() {
        String currentEmail = SecurityUtil.getCurrentEmailLogin();
        List<Friendship> friendshipList = this.friendshipRepository.getOutgoingRequest(currentEmail);
        if (CollectionUtils.isEmpty(friendshipList)) return new ArrayList<>();
        return FriendshipTransformer.toDtoList(friendshipList);
    }

    public List<FriendshipDto> getAcceptedFriend(String findingEmail) {
        String currentEmail = SecurityUtil.getCurrentEmailLogin();
        List<Friendship> friendshipList = this.friendshipRepository.getAcceptedFriend(currentEmail, findingEmail);
        if (CollectionUtils.isEmpty(friendshipList)) return new ArrayList<>();
        return FriendshipTransformer.toDtoList(friendshipList);
    }

    public List<FriendshipDto> getBlockedFriend(String findingEmail) {
        String currentEmail = SecurityUtil.getCurrentEmailLogin();
        List<Friendship> friendshipList = this.friendshipRepository.getBlockedFriend(currentEmail, findingEmail);
        if (CollectionUtils.isEmpty(friendshipList)) return new ArrayList<>();
        return FriendshipTransformer.toDtoList(friendshipList);
    }
}