package com.example.User.service;

import com.example.User.dto.request.UserBalanceRequest;
import com.example.User.dto.request.UserRequest;
import com.example.User.dto.request.UserUpdateRequest;
import com.example.User.dto.response.UserResponse;
import com.example.User.entity.User;
import com.example.User.enums.UserStatus;
import com.example.User.exceptions.EmailAlreadyExistException;
import com.example.User.exceptions.MobileNumberAlreadyExistException;
import com.example.User.exceptions.UserIdException;
import com.example.User.kafka.producer.BalanceProducer;
import com.example.User.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static com.example.User.util.CommonObjects.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BalanceProducer balanceProducer;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void test_createUser_MobileNumberExists(){

        //given
        UserRequest forUser = sampleUserRequest();
        when(userRepository.findByUserMobileNumber("9826198904")).thenReturn(Optional.of(new User()));

        //when
        userService.createUser(forUser);

        //then
        assertThrows(MobileNumberAlreadyExistException.class, () -> userService.createUser(forUser));

        //verify
        verify(userRepository).findByUserMobileNumber(forUser.getUserMobileNumber());
        verify(userRepository, never()).save(any());
        verify(balanceProducer, never()).sendUserBalanceRequest(any());
    }

    @Test
    void test_createUser_EmailExists(){

        //given
        UserRequest forUser = sampleUserRequest();
        when(userRepository.findByUserMobileNumber(forUser.getUserMobileNumber())).thenReturn(Optional.empty());
        when(userRepository.findByUserEmail(forUser.getUserEmail())).thenReturn(Optional.of(new User()));

        //when
        userService.createUser(forUser);

        //then
        assertThrows(EmailAlreadyExistException.class, () -> userService.createUser(forUser));

        //verify
        verify(userRepository).findByUserMobileNumber(forUser.getUserMobileNumber());
        verify(userRepository).findByUserEmail(forUser.getUserEmail());
        verify(userRepository, never()).save(any());
        verify(balanceProducer, never()).sendUserBalanceRequest(any());
    }

    @Test
    void test_createUser(){

        //given
        UserRequest forUser = sampleUserRequest();
        User sampleUser = sampleUser();
        UserResponse expectedResponse = sampleUserResponse();
        when(userRepository.findByUserMobileNumber(forUser.getUserMobileNumber())).thenReturn(Optional.empty());
        when(userRepository.findByUserEmail(forUser.getUserEmail())).thenReturn(Optional.empty());
        when(objectMapper.convertValue(forUser, User.class)).thenReturn(sampleUser);
        when(userRepository.save(sampleUser)).thenReturn(sampleUser);
        when(objectMapper.convertValue(sampleUser, UserResponse.class)).thenReturn(expectedResponse);

        //when
        UserResponse actualResponse = userService.createUser(forUser);

        //then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);

        //verify
        verify(userRepository).save(sampleUser);
        verify(balanceProducer).sendUserBalanceRequest(any(UserBalanceRequest.class));
    }

    @Test
    void test_getUser_UserNotExist() {

        //given
        Long userId = 95461L;
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        //when + then
        UserIdException exception = assertThrows(UserIdException.class,
                () -> userService.getUser(userId));
        assertEquals("User does not exist with ID: 99", exception.getMessage());

        //verify
        verify(userRepository).findByUserId(userId);
        verify(objectMapper, never()).convertValue(any(), eq(UserResponse.class));
    }

    @Test
    void test_getUser() {

        //given
        Long userId = 1L;
        User forUser = sampleUser();
        UserResponse expectedResponse = sampleUserResponse();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(forUser));
        when(objectMapper.convertValue(forUser, UserResponse.class)).thenReturn(expectedResponse);

        //when
        ResponseEntity<UserResponse> responseEntity = userService.getUser(userId);

        //then
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(expectedResponse, responseEntity.getBody());

        //verify
        verify(userRepository).findByUserId(userId);
        verify(objectMapper).convertValue(forUser, UserResponse.class);
    }

    @Test
    void test_updateUser_UserNotExists(){

        //given
        Long userId = 987L;
        UserUpdateRequest forUserUpdateRequest = sampleUserUpdateRequest();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        //when
        UserIdException exception = assertThrows(UserIdException.class,
                () -> userService.updateUser(forUserUpdateRequest));

        //then
        assertEquals("User not Found!", exception.getMessage());

        //verify
        verify(userRepository, never()).save(any());
    }

    @Test
    void test_updateUser(){

        //given
        Long userId = 1L;
        User forUser = sampleUser();
        UserResponse expectedResponse = sampleUserUpdateResponse();
        UserUpdateRequest forUserUpdateRequest = sampleUserUpdateRequest();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(forUser));
        when(objectMapper.convertValue(forUser, UserResponse.class)).thenReturn(expectedResponse);

        //when
        ResponseEntity<UserResponse> actualResponse = userService.updateUser(forUserUpdateRequest);

        //then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse.getBody());

        //verify
        verify(userRepository).findByUserId(userId);
        verify(objectMapper).convertValue(forUser, UserResponse.class);
    }

    @Test
    void test_deactivateUser_UserNotExist(){

        //given
        Long userId = 987L;
        when(userRepository.findByUserId(userId)).thenReturn(Optional.empty());

        //when
        UserIdException exception = assertThrows(UserIdException.class,
                () -> userService.deactivateUser(userId));

        //then
        assertEquals("User does not exist with ID: 987", exception.getMessage());

        //verify
        verify(userRepository, never()).save(any());
    }

    @Test
    void test_deactivateUser(){

        //given
        Long userId = 1L;
        User forUser = sampleUser();
        when(userRepository.findByUserId(userId)).thenReturn(Optional.of(forUser));

        //when
        ResponseEntity<String> expectedResponse = userService.deactivateUser(userId);

        //then
        assertEquals(HttpStatus.OK, expectedResponse.getStatusCode());
        assertEquals("User deactivated successfully", expectedResponse.getBody());
        assertEquals(UserStatus.INACTIVE, forUser.getUserStatus());

        //verify
        verify(userRepository).save(forUser);
    }
}
