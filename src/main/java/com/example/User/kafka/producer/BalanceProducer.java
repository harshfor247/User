package com.example.User.kafka.producer;

import com.example.User.dto.request.UserBalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BalanceProducer {

    private final KafkaTemplate<String, UserBalanceRequest> kafkaTemplate;

    public void sendUserBalanceRequest(UserBalanceRequest userBalanceRequest) {
        kafkaTemplate.send("balance-request", userBalanceRequest);
    }
}
