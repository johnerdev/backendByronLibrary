package com.lordbyron.backendByronLibrary.controller;


import com.lordbyron.backendByronLibrary.Dto.userDto.RoleAssignmentDTO;
import com.lordbyron.backendByronLibrary.Dto.userDto.UpdatePasswordDto;
import com.lordbyron.backendByronLibrary.Dto.userDto.UserStatusDto;
import com.lordbyron.backendByronLibrary.entity.Role;
import com.lordbyron.backendByronLibrary.entity.Users;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.services.UsersServiceImpl;
import com.lordbyron.backendByronLibrary.services.UsersServices;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
/*@CrossOrigin("*")*/
@RequestMapping("/users")
public class UsersController {
    private final UsersServices usersService;
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);

    public UsersController(UsersServices usersService) {
        this.usersService = usersService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        try {
            Iterable<Users> users = usersService.getUsers();
            return ResponseEntity.ok(users);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?>addUsers(@RequestBody Users users){
        try {
            usersService.saveUser(users);
            return ResponseEntity.ok(Map.of("message", "Usuario creado con exito"));
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

   /* @PostMapping("/login")
    public UserDto login(@RequestBody LoginDto login){
        return  usersService.loginUser(login.getEmail() ,login.getPassword());
    }*/
   @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateUser(@RequestBody Users user) {
        try {
            return usersService.updateUser(user);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/count")
    public Long countUsers(){
        return usersService.countUsers();
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("find/{email}")
    public ResponseEntity<?> getUser(@PathVariable("email") String email) {
        try {
            Users user = usersService.getUser(email);
            return ResponseEntity.ok(user);
        } catch (ExceptionMessage e) {
            log.error("Error fetching user: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            return ResponseEntity.ok(Map.of("message", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add-role")
    public ResponseEntity<?> addRoleToUser(@RequestBody @Valid RoleAssignmentDTO roleAssignmentDTO) {
        log.info("Request to add role '{}' to user '{}'", roleAssignmentDTO.getRoleName(), roleAssignmentDTO.getEmail());
        try {
            usersService.addRoleToUser(roleAssignmentDTO);
            return ResponseEntity.ok(Map.of("message", "Rol asignado exitosamente al usuario"));
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove-role")
    public ResponseEntity<Map<String, String>> removeRoleFromUser(@RequestBody RoleAssignmentDTO removeRoleDto) {
        try {
            usersService.removeRoleFromUser(removeRoleDto);
            return ResponseEntity.ok(Map.of("message", "Rol eliminado con éxito!"));
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/status")
    public ResponseEntity<Map<String, String>> changeUserStatus(@RequestBody UserStatusDto changeUserStatusDto) {
        try {
            return usersService.changeUserStatus(changeUserStatusDto);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        try {
            return usersService.changePassword(updatePasswordDto);
        } catch (ExceptionMessage e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/role/save")
    public ResponseEntity<?> saveRole(@RequestBody Role role) {
        try {
            Role savedRole = usersService.saveRole(role);
            return ResponseEntity.ok(Map.of("message", "Rol creado con éxito!", "role", savedRole));
        } catch (ExceptionMessage ex) {
            log.error("Error creating role: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
        }
    }

}