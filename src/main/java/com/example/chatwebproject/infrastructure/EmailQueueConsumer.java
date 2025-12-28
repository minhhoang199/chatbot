package com.example.chatwebproject.infrastructure;

import com.example.chatwebproject.model.entity.OTPVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class EmailQueueConsumer implements Runnable {
    private final JavaMailSender mailSender;

    @Override
    public void run() {
        while (true) {
            try {
                // BLOCK until having a task
                System.out.println("Offer ok=" + EmailQueue.QUEUE.size());
                OTPVerification task = EmailQueue.QUEUE.take();
                // submit into executor
                EmailExecutor.EXECUTOR.submit(() -> sendEmail(task));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
//                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendEmail(OTPVerification task) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("hoangminhst19s99@gmail.com");
        simpleMailMessage.setTo(task.getEmail());
        simpleMailMessage.setSubject("Send OTP from system");
        simpleMailMessage.setText("This is your OTP: " + task.getOtpCode());

        this.mailSender.send(simpleMailMessage);

    }
}
