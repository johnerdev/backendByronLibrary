package com.lordbyron.backendByronLibrary;

import com.lordbyron.backendByronLibrary.Dto.UpdatePasswordDto;
import com.lordbyron.backendByronLibrary.Dto.UserStatusDto;
import com.lordbyron.backendByronLibrary.entity.Users;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.repository.RoleRepository;
import com.lordbyron.backendByronLibrary.repository.UsersRepository;
import com.lordbyron.backendByronLibrary.services.UsersServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersServiceImplTest {

    @InjectMocks
    private UsersServiceImpl usersService;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveUser_ShouldSaveUserSuccessfully() {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setPassword("password");

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(usersRepository.save(user)).thenReturn(user);

        ResponseEntity<Map<String, String>> response = usersService.saveUser(user);

        assertNotNull(response);
        assertEquals("Usuario creado con éxito!", response.getBody().get("message"));
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    void saveUser_ShouldThrowException_WhenEmailAlreadyExists() {
        Users user = new Users();
        user.setEmail("test@example.com");

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ExceptionMessage exception = assertThrows(ExceptionMessage.class, () -> usersService.saveUser(user));
        assertEquals("El correo electrónico ya está registrado: test@example.com", exception.getMessage());
    }

    @Test
    void getUsers_ShouldReturnUsers() {
        Users user = new Users();
        user.setEmail("test@example.com");

        when(usersRepository.findAll()).thenReturn(Collections.singletonList(user));

        var users = usersService.getUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        verify(usersRepository, times(1)).findAll();
    }

    @Test
    void getUsers_ShouldThrowException_WhenNoUsersFound() {
        when(usersRepository.findAll()).thenReturn(Collections.emptyList());

        ExceptionMessage exception = assertThrows(ExceptionMessage.class, () -> usersService.getUsers());
        assertEquals("No se encontraron usuarios registrados", exception.getMessage());
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        Users existingUser = new Users();
        existingUser.setId(1L);
        existingUser.setName("Old Name");

        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setName("New Name");

        when(usersRepository.findById(updatedUser.getId())).thenReturn(Optional.of(existingUser));

        ResponseEntity<Map<String, String>> response = usersService.updateUser(updatedUser);

        assertNotNull(response);
        assertEquals("Usuario actualizado con éxito!", response.getBody().get("message"));
        assertEquals("New Name", existingUser.getName());
        verify(usersRepository, times(1)).save(existingUser);
    }

    @Test
    void changeUserStatus_ShouldUpdateStatusSuccessfully() {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setEnabled(false);

        UserStatusDto statusDto = new UserStatusDto("test@example.com", true);

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> response = usersService.changeUserStatus(statusDto);

        assertNotNull(response);
        assertEquals("Estado del usuario modificado con éxito!", response.getBody().get("message"));
        assertTrue(user.isEnabled());
        verify(usersRepository, times(1)).save(user);
    }

    @Test
    void changePassword_ShouldUpdatePasswordSuccessfully() {
        Users user = new Users();
        user.setEmail("test@example.com");

        UpdatePasswordDto passwordDto = new UpdatePasswordDto("test@example.com", "newPassword");

        when(usersRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        ResponseEntity<Map<String, String>> response = usersService.changePassword(passwordDto);

        assertNotNull(response);
        assertEquals("Password actualizado con éxito!", response.getBody().get("message"));
        assertEquals("newPassword", user.getPassword());
        verify(usersRepository, times(1)).save(user);
    }
}
