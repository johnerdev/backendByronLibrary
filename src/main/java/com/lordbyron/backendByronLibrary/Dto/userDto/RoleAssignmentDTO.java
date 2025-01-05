package com.lordbyron.backendByronLibrary.Dto.userDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleAssignmentDTO {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "El nombre del rol no puede estar vacío")
    private String roleName;
}