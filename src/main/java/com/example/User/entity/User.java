package com.example.User.entity;

import com.example.User.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;

    private String userName;
    private String userMobileNumber;
    private String userEmail;
    private Integer userAge;
    private Double userBalance;
}