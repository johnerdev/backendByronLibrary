package com.lordbyron.backendByronLibrary.Dto.userDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class UserDto {
    private String name;
    private String rol;

    public UserDto() {

    }
}
