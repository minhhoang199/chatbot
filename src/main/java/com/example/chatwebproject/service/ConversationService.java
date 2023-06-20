package com.example.chatwebproject.service;

import com.example.chatwebproject.model.Connection;
import com.example.chatwebproject.model.Conversation;
import com.example.chatwebproject.model.Account;
import com.example.chatwebproject.model.enums.ConnectionStatus;
import com.example.chatwebproject.model.enums.ConversationStatus;
import com.example.chatwebproject.model.enums.ConversationType;
import com.example.chatwebproject.model.vm.GroupConversationVM;
import com.example.chatwebproject.model.vm.InviteeVM;
import com.example.chatwebproject.model.vm.PrivateConversationVM;
import com.example.chatwebproject.repository.ConnectionRepository;
import com.example.chatwebproject.repository.ConversationRepository;
import com.example.chatwebproject.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ConversationService {
    private ConversationRepository conversationRepository;
    private AccountRepository userRepository;
    private ConnectionRepository connectionRepository;

    public ConversationService(ConversationRepository conversationRepository,
                               AccountRepository accountRepository,
                               ConnectionRepository connectionRepository) {
        this.conversationRepository = conversationRepository;
        this.userRepository = accountRepository;
        this.connectionRepository = connectionRepository;
    }

    private void validatePhone(String phone) {
        Pattern pattern = Pattern.compile("^0\\d{9}$|^84\\d{9}$");
        Matcher matcher = pattern.matcher(phone);
        if (!matcher.find()) {
            throw new RuntimeException(phone + ": Invalid phone format");
        }
    }


    public void addNewPrivateConversation(PrivateConversationVM privateConversationVM) {
        if (privateConversationVM != null) {
            //validation
            String name = privateConversationVM.getName();
//            if (name == null ||
//                    name.length() <= 1) {
//                throw new RuntimeException("Invalid name");
//            }

            String invitorPhone = privateConversationVM.getInvitorPhone();
//            validatePhone(invitorPhone);
            Optional<Account> optionalInvitor = this.userRepository.findByPhone(invitorPhone);
            if (optionalInvitor.isEmpty()) {
                throw new RuntimeException("Not found invitor");
            }
            Account invitor = optionalInvitor.get();

            String inviteePhone = privateConversationVM.getInviteePhone();
//            validatePhone(inviteePhone);
            Optional<Account> optionalInvitee = this.userRepository.findByPhone(inviteePhone);
            if (optionalInvitee.isEmpty()) {
                throw new RuntimeException("Not found invitee");
            }
            Account invitee = optionalInvitee.get();

            List<Conversation> conversationList = this.conversationRepository.findByPhoneAndType(invitorPhone, ConversationType.PRIVATE_CHAT);
            for (Conversation conversation: conversationList){
                if (conversation.getAccounts().contains(invitee)){
                    throw new RuntimeException("Private chat already existed");
                }
            }

            Conversation newConversation = new Conversation();
            newConversation.setName(name);
            newConversation.setConversationType(ConversationType.PRIVATE_CHAT);

            //add invitor to the list
            Set<Account> users = new HashSet<>();

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

            invitor.getConversations().add(newConversation);
            //this.userRepository.save(invitor);
            users.add(invitor);

            invitee.getConversations().add(newConversation);
            //this.userRepository.save(invitee);
            users.add(invitee);

            newConversation.setAccounts(users);
            newConversation.setConversationStatus(ConversationStatus.ENABLE);
            this.userRepository.saveAll(users);
            this.conversationRepository.save(newConversation);
        }
    }


    public void addGroupConversation(GroupConversationVM conversationVM) {
        if (conversationVM != null) {
            //validation
            String name = conversationVM.getName();
//            if (name == null ||
//                    name.length() <= 1) {
//                throw new RuntimeException("Invalid name");
//            }

            String invitorPhone = conversationVM.getInvitorPhone();
//            validatePhone(invitorPhone);
            Optional<Account> optionalInvitor = this.userRepository.findByPhone(invitorPhone);
            if (optionalInvitor.isEmpty()) {
                throw new RuntimeException("Not found invitor");
            }

            Account invitor = optionalInvitor.get();

            List<String> phones = conversationVM.getPhones();
//            if (phones == null || phones.size() < 2) {
//                throw new RuntimeException("Invalid list of phones");
//            }

            Conversation newConversation = new Conversation();
            newConversation.setName(name);
            newConversation.setConversationType(ConversationType.GROUP_CHAT);

            //add invitor to the list
            Set<Account> users = new HashSet<>();
            invitor.getConversations().add(newConversation);
//            this.userRepository.save(invitor);
            users.add(invitor);

            //add users to the list
            for (String phone : phones
            ) {
                validatePhone(phone);

                Optional<Account> optionalUser = this.userRepository.findByPhone(phone);
                if (optionalUser.isEmpty()) {
                    throw new RuntimeException("Not found account by phone");
                }
                Optional<Connection> optionalConnectionWithUser = this.connectionRepository.findByUsersAndStatus(
                        conversationVM.getInvitorPhone(),
                        phone,
                        ConnectionStatus.CONNECTED);
                Optional<Connection> optionalConnectionWithInvitor = this.connectionRepository.findByUsersAndStatus(
                        phone,
                        conversationVM.getInvitorPhone(),
                        ConnectionStatus.CONNECTED);
                if (optionalConnectionWithInvitor.isEmpty() || optionalConnectionWithUser.isEmpty()) {
                    throw new RuntimeException("Invalid connection between invitor and User");
                }

                Account currentUser = optionalUser.get();
                currentUser.getConversations().add(newConversation);
//                this.userRepository.save(currentUser);
                users.add(currentUser);
            }

            newConversation.setAccounts(users);
            newConversation.setConversationStatus(ConversationStatus.ENABLE);
            this.userRepository.saveAll(users);
            this.conversationRepository.save(newConversation);
        }
    }

    public List<Conversation> getAll() {
        return conversationRepository.findAll();
    }

    public void addMoreUser(InviteeVM inviteeVm, Long conversationId) {
        String invitorPhone = inviteeVm.getInvitorPhone();
        validatePhone(invitorPhone);

        Optional<Account> optionalInvitor = this.userRepository.findByPhone(invitorPhone);
        if (optionalInvitor.isEmpty()) {
            throw new RuntimeException("Not found invitor");
        }
        Account invitor = optionalInvitor.get();

        var optConversation = conversationRepository.findById(conversationId);
        if (optConversation.isEmpty()) {
            throw new RuntimeException("Not found conversation");
        } else if (optConversation.get().getConversationType() == ConversationType.PRIVATE_CHAT) {
            throw new RuntimeException("Invalid conversation Type: private chat can not add more users");
        }
        Conversation currentConversation = optConversation.get();
        Set<Account> users = currentConversation.getAccounts();
        if (!users.contains(invitor)) {
            throw new RuntimeException("Invitor does not belong to conversation");
        }

        List<String> inviteePhones = inviteeVm.getInviteePhones();
        for (String phone : inviteePhones
        ) {
            validatePhone(phone);

            Optional<Account> optionalUser = this.userRepository.findByPhone(phone);
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

            Account currentUser = optionalUser.get();

            currentUser.getConversations().add(currentConversation);
            this.userRepository.save(currentUser);
            users.add(currentUser);
        }
        currentConversation.setAccounts(users);
        this.conversationRepository.save(currentConversation);
    }

    public void changeConversationStatus(Long id, ConversationStatus conversationStatus) {
        if (id == null || id <= 0) {
            throw new RuntimeException("Invalid Id");
        }

        Optional<Conversation> optionalConversation = this.conversationRepository.findById(id);
        if (optionalConversation.isEmpty()) {
            throw new RuntimeException("Not found conversation");
        }

        Conversation currentConversation = optionalConversation.get();
        currentConversation.setConversationStatus(conversationStatus);
        this.conversationRepository.save(currentConversation);
    }
}