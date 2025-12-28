package com.example.chatwebproject.infrastructure;

import com.example.chatwebproject.model.entity.OTPVerification;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class EmailQueue {
    public static final BlockingQueue<OTPVerification> QUEUE =
            new ArrayBlockingQueue<>(200);
}
