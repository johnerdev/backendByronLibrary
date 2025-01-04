package com.lordbyron.backendByronLibrary.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class LoginDto {
    private String email;
    private String password;
}