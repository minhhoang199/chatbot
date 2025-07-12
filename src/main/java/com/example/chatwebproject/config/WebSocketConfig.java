package com.example.chatwebproject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
/*
This annotation enables WebSocket message handling, backed by a message broker.
It indicates that the application will use a message broker to route messages to various destinations.
 */
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    /*
    The Simple Text Oriented Messaging Protocol (STOMP) is a lightweight, text-based protocol that provides
    a simple way for clients to communicate with a message broker over a WebSocket connection.
    - WebSocket abstraction: While WebSocket provides a full-duplex communication channel between a client and a server,
     it's a low-level protocol. STOMP adds a layer of abstraction on top of WebSocket, making it easier to work with by
     providing features like message headers, destinations, and subscriptions.
    - Cross-platform compatibility: STOMP is designed to be platform-agnostic and can be used with any
    programming language or platform that supports WebSocket connections.
    - Pub/sub messaging: STOMP supports publish/subscribe messaging, where clients can subscribe to specific
    destinations (topics) to receive messages. This is useful for scenarios like broadcasting messages to
    multiple clients or implementing real-time updates in chat applications.
    - Ease of use: STOMP messages are human-readable and easy to understand, making it straightforward for developers
    to work with. It uses simple text-based commands like CONNECT, SUBSCRIBE, SEND, and DISCONNECT, which are similar
    to HTTP methods, making it easy to grasp for developers familiar with web technologies.
    - Widespread support: STOMP is supported by many messaging brokers and WebSocket libraries, making it a popular
    choice for building real-time web applications.
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                //.setAllowedOrigins("http://localhost:4200") // Allow your Angular app's origin
                .setAllowedOrigins("http://localhost:4200") // For lan wifi
                .withSockJS();
    }

/*
configureMessageBroker(MessageBrokerRegistry registry): This method configures the message broker.
The message broker is responsible for routing messages from one client to another. In this configuration:
- registry.enableSimpleBroker("/topic"): It enables a simple in-memory message broker with a prefix of "/topic".
This means that messages sent to destinations prefixed with "/topic" will be routed to subscribers.
- registry.setApplicationDestinationPrefixes("/app"): It sets the prefix for messages that are bound for methods
annotated with @MessageMapping. In this case, messages sent to destinations prefixed with "/app" will be routed to
@MessageMapping annotated methods.
 */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
