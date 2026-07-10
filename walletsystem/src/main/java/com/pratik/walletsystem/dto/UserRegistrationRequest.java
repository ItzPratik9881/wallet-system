package com.pratik.walletsystem.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegistrationRequest {

    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
}