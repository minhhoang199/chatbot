package com.example.chatwebproject.service;

import com.example.chatwebproject.model.Connection;
import com.example.chatwebproject.model.User;
import com.example.chatwebproject.model.enums.ConnectionStatus;
import com.example.chatwebproject.model.dto.ConnectionStatusDto;
import com.example.chatwebproject.model.dto.RequestConnectionDto;
import com.example.chatwebproject.repository.ConnectionRepository;
import com.example.chatwebproject.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConnectionService {
    private ConnectionRepository connectionRepository;
    private UserRepository userRepository;

    public ConnectionService(ConnectionRepository connectionRepository, UserRepository userRepository) {
        this.connectionRepository = connectionRepository;
        this.userRepository = userRepository;
    }

//    private void validatePhone(String phone) {
//        Pattern pattern = Pattern.compile("^0\\d{9}$|^84\\d{9}$");
//        Matcher matcher = pattern.matcher(phone);
//        if (!matcher.find()) {
//            throw new RuntimeException(phone + ": Invalid phone format");
//        }
//    }

    public void createConnection(RequestConnectionDto requestConnectionVM) {
        String followingPhone = requestConnectionVM.getFollowingPhone();
//        validatePhone(followingPhone);

        String followedPhone = requestConnectionVM.getFollowedPhone();
//        validatePhone(followedPhone);

        Optional<Connection> otpConnection = this.connectionRepository.findByUsersAndStatus(followingPhone,
                followedPhone,
                ConnectionStatus.CONNECTED);
        if (otpConnection.isPresent()) {
            throw new RuntimeException("Connection already existed");
        }

        Optional<User> otpFollowingUser = this.userRepository.findByPhone(followingPhone);
        if (otpFollowingUser.isEmpty()) {
            throw new RuntimeException("Not found following user");
        }

        Optional<User> otpFollowedUser = this.userRepository.findByPhone(followedPhone);
        if (otpFollowedUser.isEmpty()) {
            throw new RuntimeException("Not found followed user");
        }

        Connection newConnection = new Connection();
        newConnection.setFollowedUser(otpFollowedUser.get());
        newConnection.setFollowingUser(otpFollowingUser.get());
        newConnection.setConnectionStatus(ConnectionStatus.CONNECTED);

        connectionRepository.save(newConnection);
    }


    public void changeConnectionStatus(ConnectionStatusDto connectionStatusDto) {
        String followingPhone = connectionStatusDto.getFollowingPhone();
        String followedPhone = connectionStatusDto.getFollowedPhone();
        ConnectionStatus connectionStatus = connectionStatusDto.getConnectionStatus();
        if (connectionStatus == null) {
            throw new RuntimeException("Connection status is null");
        }

//        validatePhone(followingPhone);
//        validatePhone(followedPhone);

        Optional<Connection> otpConnection = this.connectionRepository.findByUsers(followingPhone, followedPhone);
        if (otpConnection.isEmpty()) {
            throw new RuntimeException("Not found connection");
        }

        Connection currentConnection = otpConnection.get();
        currentConnection.setConnectionStatus(connectionStatus);
        this.connectionRepository.save(currentConnection);
    }
}