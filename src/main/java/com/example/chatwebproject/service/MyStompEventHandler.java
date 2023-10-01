package com.example.chatwebproject.service;

import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.stomp.*;

public class MyStompEventHandler extends StompSessionHandlerAdapter {
    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        System.out.println("Client connected.");
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        System.err.println("Error occurred in transport layer: " + exception.getMessage());
    }

    @Override
    public void handleException(StompSession session, @Nullable StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        System.err.println("Exception occurred: " + exception.getMessage());
    }

}
