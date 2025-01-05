package com.lordbyron.backendByronLibrary.Dto.userDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatePasswordDto {
    @NotBlank
    private String email;
    @NotBlank
    private String newPassword;
}
