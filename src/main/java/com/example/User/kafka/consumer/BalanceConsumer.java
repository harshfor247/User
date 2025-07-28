package com.example.User.kafka.consumer;

import com.example.User.dto.response.UserBalanceUpdatedResponse;
import com.example.User.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceConsumer {

    private final UserRepository userRepository;

    @KafkaListener(topics = "balance-response", groupId = "user-group")
    public void listenToUpdatedBalance(UserBalanceUpdatedResponse response) {
        log.info("Received balance update: {}", response);

        userRepository.findById(response.getUserId()).ifPresent(user -> {
            user.setUserBalance(response.getUpdatedUserBalance());
            userRepository.save(user);
            log.info("User balance updated for userId {}", user.getUserId());
        });
    }
}
