package com.example.User.service;

import com.example.User.dto.request.UserBalanceRequest;
import com.example.User.dto.request.UserUpdateRequest;
import com.example.User.dto.request.UserRequest;
import com.example.User.dto.response.UserResponse;
import com.example.User.entity.User;
import com.example.User.enums.UserStatus;
import com.example.User.exceptions.EmailAlreadyExistException;
import com.example.User.exceptions.MobileNumberAlreadyExistException;
import com.example.User.exceptions.UserIdException;
import com.example.User.kafka.producer.BalanceProducer;
import com.example.User.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BalanceProducer balanceProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserResponse createUser(UserRequest userRequest) {

        Optional<User> existing = userRepository.findByUserMobileNumber(userRequest.getUserMobileNumber());

        if (existing.isPresent()) {
            throw new MobileNumberAlreadyExistException("Mobile number already registered");
        }

        Optional<User> sameEmail = userRepository.findByUserEmail(userRequest.getUserEmail());

        if (sameEmail.isPresent()) {
            throw new EmailAlreadyExistException("Email already registered");
        }


        // 1. Convert DTO to Entity
        User user = objectMapper.convertValue(userRequest, User.class);
        user.setUserStatus(UserStatus.ACTIVE);

        // 2. Save to DB
        User savedUser = userRepository.save(user);

        // 3. Send user balance to Kafka (balanceRequest topic)
        UserBalanceRequest balanceRequest = UserBalanceRequest.builder()
                .userId(savedUser.getUserId())
                .userBalance(savedUser.getUserBalance())
                .build();

        balanceProducer.sendUserBalanceRequest(balanceRequest);

        // 4. Convert Entity to Response DTO
        UserResponse userResponse = objectMapper.convertValue(savedUser, UserResponse.class);
        return userResponse;
    }


    public ResponseEntity<UserResponse> getUser(Long userId) {
        return userRepository.findByUserId(userId)
                .map(user -> {
                    UserResponse userResponse = objectMapper.convertValue(user, UserResponse.class);
                    return ResponseEntity.ok(userResponse);
                })
                .orElseThrow(() -> new UserIdException("User does not exist with ID: " + userId));
    }

    public ResponseEntity<UserResponse> updateUser(UserUpdateRequest userUpdateRequest) {
        return userRepository.findByUserId(userUpdateRequest.getUserId())
                .map(existingUser -> {
                    if (userUpdateRequest.getUserName() != null)
                        existingUser.setUserName(userUpdateRequest.getUserName());

                    if (userUpdateRequest.getUserEmail() != null )
                        existingUser.setUserEmail(userUpdateRequest.getUserEmail());

                    if (userUpdateRequest.getUserMobileNumber() != null)
                        existingUser.setUserMobileNumber(userUpdateRequest.getUserMobileNumber());

                    if (userUpdateRequest.getUserAge() != null)
                        existingUser.setUserAge(userUpdateRequest.getUserAge());

                    if (userUpdateRequest.getUserBalance() != null)
                        existingUser.setUserBalance(userUpdateRequest.getUserBalance());

                    User updated = userRepository.save(existingUser);

                    // Convert to UserResponse
                    UserResponse response = objectMapper.convertValue(updated, UserResponse.class);
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> new UserIdException("User not Found!"));
    }


    public ResponseEntity<String> deactivateUser(Long userId) {
        return userRepository.findByUserId(userId)
                .map(user -> {
                    user.setUserStatus(UserStatus.INACTIVE);
                    userRepository.save(user);
                    return ResponseEntity.ok("User deactivated successfully");
                })
                .orElseThrow(() -> new UserIdException("User does not exist with ID: " + userId));
    }
}