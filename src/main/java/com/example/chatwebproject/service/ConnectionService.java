package com.example.chatwebproject.service;


import com.example.chatwebproject.constant.DomainCode;
import com.example.chatwebproject.exception.ChatApplicationException;
import com.example.chatwebproject.exception.ValidationRequestException;
import com.example.chatwebproject.model.entity.Connection;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.request.ChangeConnectionStatusRequest;
import com.example.chatwebproject.model.request.CreateConnectionRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.enums.ConnectionStatus;
import com.example.chatwebproject.model.response.BaseResponse;
import com.example.chatwebproject.model.response.ChangeConnectionStatusResponse;
import com.example.chatwebproject.model.response.CreateConnectionResponse;
import com.example.chatwebproject.model.response.RespFactory;
import com.example.chatwebproject.repository.ConnectionRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.utils.Constant;
import com.example.chatwebproject.utils.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ConnectionService {
    private ConnectionRepository connectionRepository;
    private UserRepository userRepository;
    private RoomService roomService;
    private RespFactory respFactory;


    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    //TODO: API tạo request kết bạn (connection)
    //TODO: API Đồng ý/Từ chối kết bạn (changeStatus connection) -> Nếu đồng ý thì tạo thêm 1 private_chat
    @Transactional
    public ResponseEntity<BaseResponse> createConnection(CreateConnectionRequest request) {
        String requestEmail = request.getRequestEmail();
        String acceptedEmail = request.getAcceptedEmail();
        ResponseEntity<BaseResponse> response;

        try {
            Optional<Connection> otpConnection = this.connectionRepository.findByUsersAndStatus(requestEmail,
                    acceptedEmail,
                    ConnectionStatus.DISCONNECTED);
            if (otpConnection.isPresent()) {
                return this.respFactory.failWithBadInputParameter(new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{ "Connection already existed"}, null));
            }

            Optional<User> otpRequestUser = this.userRepository.findByEmailAndDelFlg(requestEmail);
            if (otpRequestUser.isEmpty()) {
                return this.respFactory.failNotFoundData(new ValidationRequestException(DomainCode.NOT_FOUND_DATA, new Object[]{"Not found request user: " + requestEmail}, null));
            }

            Optional<User> otpAcceptedUser = this.userRepository.findByEmailAndDelFlg(acceptedEmail);
            if (otpAcceptedUser.isEmpty()) {
                return this.respFactory.failNotFoundData(new ValidationRequestException(DomainCode.NOT_FOUND_DATA, new Object[]{"Not found accept user: " + acceptedEmail}, null));
            }

            Connection newConnection = new Connection();
            newConnection.setAccepted(Constant.NO);
            newConnection.setAcceptedUser(otpAcceptedUser.get());
            newConnection.setRequestUser(otpRequestUser.get());
            newConnection.setConnectionStatus(ConnectionStatus.DISCONNECTED);

            response = this.respFactory.success(connectionRepository.save(newConnection));
        } catch (Exception e) {
            return this.respFactory.failWithInternalException(new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR));
        }
        return response;
    }

    //TODO: Tìm solution để FE nhận notification
    //TODO: check nếu có 1 connection khác của 2 users thì cảnh báo: "user request đã gửi request cho bạn"

    @Transactional
    public ResponseEntity<BaseResponse> changeConnectionStatus(ChangeConnectionStatusRequest request) {
        ResponseEntity<BaseResponse> response;
        try {
            Optional<Connection> otpConnection = this.connectionRepository.findById(request.getConnectionId());
            if (otpConnection.isEmpty()) {
                return this.respFactory.failNotFoundData(new ValidationRequestException(DomainCode.NOT_FOUND_DATA, new Object[]{"Not found connection: " + request.getConnectionId()}, null));
            }

            //Check valid users ?
            Connection connection = otpConnection.get();
            if (connection.getAcceptedUser() == null || connection.getRequestUser() == null) {
                return this.respFactory.failWithValidationRequestException(new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"Invalid user of connection: " + request.getConnectionId()}, null));
            }

            //TODO: use redis to store session data
            Long userId = SecurityUtil.getCurrentUserIdLogin();
            if (!Objects.equals(userId, connection.getAcceptedUser().getId())) {
                return this.respFactory.failWithValidationRequestException(new ValidationRequestException(DomainCode.INVALID_PARAMETER, new Object[]{"This user can not accept this request connection: " + request.getConnectionId()}, null));
            }

            //Update connection
            connection.setConnectionStatus(request.getConnectionStatus());
            this.connectionRepository.save(connection);

            //Tạo 1 private room giữa 2 users
            if (Constant.NO.equals(connection.getAccepted()) && ConnectionStatus.CONNECTED.equals(request.getConnectionStatus())) {
                User requestUser = connection.getRequestUser();
                User acceptedUser = connection.getAcceptedUser();
                SaveRoomRequest saveRoomRequest = new SaveRoomRequest();
                StringBuilder sbRoomName = new StringBuilder("");
                sbRoomName.append(requestUser.getId()).append(":").append(requestUser.getUsername()).append("#")
                        .append(acceptedUser.getId()).append(":").append(acceptedUser.getUsername());
                saveRoomRequest.setName(sbRoomName.toString());
                saveRoomRequest.setEmails(List.of(requestUser.getEmail(), acceptedUser.getEmail()));
                roomService.addNewRoom(saveRoomRequest);
            }
        } catch (Exception e) {
            return this.respFactory.failWithInternalException(new ChatApplicationException(DomainCode.INTERNAL_SERVICE_ERROR, new Object[]{"Create connection occur error: " + e.getMessage()}));
        }
        return this.respFactory.success();
    }
}