package com.example.chatwebproject.infrastructure;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EmailExecutor {
    public static final ExecutorService EXECUTOR =
            new ThreadPoolExecutor(
                    2,               // core
                    5,               // max
                    30, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(100),
                    new ThreadPoolExecutor.CallerRunsPolicy()
            );
}
