package com.example.chatwebproject.service;


import com.example.chatwebproject.model.entity.Connection;
import com.example.chatwebproject.model.entity.User;
import com.example.chatwebproject.model.request.ChangeConnectionStatusRequest;
import com.example.chatwebproject.model.request.CreateConnectionRequest;
import com.example.chatwebproject.model.request.SaveRoomRequest;
import com.example.chatwebproject.model.enums.ConnectionStatus;
import com.example.chatwebproject.model.response.ChangeConnectionStatusResponse;
import com.example.chatwebproject.model.response.CreateConnectionResponse;
import com.example.chatwebproject.model.response.Result;
import com.example.chatwebproject.repository.ConnectionRepository;
import com.example.chatwebproject.repository.UserRepository;
import com.example.chatwebproject.utils.Constant;
import com.example.chatwebproject.utils.SecurityUtil;
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


    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

    //TODO: API tạo request kết bạn (connection)
    //TODO: API Đồng ý/Từ chối kết bạn (changeStatus connection) -> Nếu đồng ý thì tạo thêm 1 private_chat
    @Transactional
    public CreateConnectionResponse createConnection(CreateConnectionRequest request) {
        CreateConnectionResponse response = new CreateConnectionResponse();
        Result result = new Result("200", "Succeed", null);
        String requestEmail = request.getRequestEmail();
        String acceptedEmail = request.getAcceptedEmail();

        try {
            Optional<Connection> otpConnection = this.connectionRepository.findByUsersAndStatus(requestEmail,
                    acceptedEmail,
                    ConnectionStatus.DISCONNECTED);
            if (otpConnection.isPresent()) {
                result = new Result("400", "Connection already existed", null);
                response.setResult(result);
                return response;
            }

            Optional<User> otpRequestUser = this.userRepository.findByEmailAndDelFlg(requestEmail);
            if (otpRequestUser.isEmpty()) {
                result = new Result("404", "Not found request user: " + requestEmail, null);
                response.setResult(result);
                return response;
            }

            Optional<User> otpAcceptedUser = this.userRepository.findByEmailAndDelFlg(acceptedEmail);
            if (otpAcceptedUser.isEmpty()) {
                result = new Result("404", "Not found accept user: " + acceptedEmail, null);
                response.setResult(result);
                return response;
            }

            Connection newConnection = new Connection();
            newConnection.setAccepted(Constant.NO);
            newConnection.setAcceptedUser(otpAcceptedUser.get());
            newConnection.setRequestUser(otpRequestUser.get());
            newConnection.setConnectionStatus(ConnectionStatus.DISCONNECTED);

            connectionRepository.save(newConnection);
        } catch (Exception e) {
            result = new Result("500", "Create connection occur error: " + e.getMessage(), null);
        }
        response.setResult(result);
        return response;
    }

    //TODO: Tìm solution để FE nhận notification
    //TODO: check nếu có 1 connection khác của 2 users thì cảnh báo: "user request đã gửi request cho bạn"

    @Transactional
    public ChangeConnectionStatusResponse changeConnectionStatus(ChangeConnectionStatusRequest request) {
        ChangeConnectionStatusResponse response = new ChangeConnectionStatusResponse();
        Result result = new Result();
        try {
            Optional<Connection> otpConnection = this.connectionRepository.findById(request.getConnectionId());
            if (otpConnection.isEmpty()) {
                result = new Result("404", "Not found connection: " + request.getConnectionId(), null);
                response.setResult(result);
                return response;
            }

            //Check valid users ?
            Connection connection = otpConnection.get();
            if (connection.getAcceptedUser() == null || connection.getRequestUser() == null) {
                result = new Result("CB1001", "Invalid user of connection: " + request.getConnectionId(), null);
                response.setResult(result);
                return response;
            }

            //TODO: use redis to store session data
            Long userId = SecurityUtil.getCurrentUserIdLogin();
            if (!Objects.equals(userId, connection.getAcceptedUser().getId())) {
                result = new Result("CB1002", "This user can not accept this request connection: " + request.getConnectionId(), null);
                response.setResult(result);
                return response;
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
            result = new Result("500", "Create connection occur error: " + e.getMessage(), null);
        }
        response.setResult(result);
        return response;
    }
}