package com.lordbyron.backendByronLibrary.services;

import com.lordbyron.backendByronLibrary.Dto.RoleAssignmentDTO;
import com.lordbyron.backendByronLibrary.Dto.UpdatePasswordDto;
import com.lordbyron.backendByronLibrary.Dto.UserDto;
import com.lordbyron.backendByronLibrary.Dto.UserStatusDto;
import com.lordbyron.backendByronLibrary.entity.Role;
import com.lordbyron.backendByronLibrary.entity.Users;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface UsersServices {
    ResponseEntity<?> saveUser(Users users);
    Role saveRole (Role role);
    void addRoleToUser(RoleAssignmentDTO roleAssignmentDTO);
     void removeRoleFromUser(RoleAssignmentDTO removeRoleDto);
    Users getUser(String email);
     List<Users> getUsers();
     ResponseEntity<Map<String, String>> updateUser(final Users user);
     ResponseEntity<Map<String, String>> changeUserStatus(UserStatusDto changeUserStatusDto);
    ResponseEntity<Map<String, String>> changePassword(UpdatePasswordDto updatePassword);
    Long countUsers();
    Long countRoles();
    UserDto loginUser(String email, String password);


}
