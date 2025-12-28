package com.example.chatwebproject.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class EmailQueueStarter {
    private final EmailQueueConsumer emailQueueConsumer;

    @PostConstruct
    public void start() {
        Thread consumerThread = new Thread(emailQueueConsumer);
        consumerThread.setDaemon(true);
        consumerThread.start();
    }
}
