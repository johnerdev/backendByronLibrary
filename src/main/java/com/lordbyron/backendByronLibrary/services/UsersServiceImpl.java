package com.lordbyron.backendByronLibrary.services;


import com.lordbyron.backendByronLibrary.Dto.userDto.RoleAssignmentDTO;
import com.lordbyron.backendByronLibrary.Dto.userDto.UpdatePasswordDto;
import com.lordbyron.backendByronLibrary.Dto.userDto.UserDto;
import com.lordbyron.backendByronLibrary.Dto.userDto.UserStatusDto;
import com.lordbyron.backendByronLibrary.entity.Role;
import com.lordbyron.backendByronLibrary.entity.Users;
import com.lordbyron.backendByronLibrary.repository.RoleRepository;
import com.lordbyron.backendByronLibrary.repository.UsersRepository;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class UsersServiceImpl implements UsersServices{
    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);
    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersServiceImpl(UsersRepository usersRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseEntity<Map<String, String>> saveUser(final Users user) {
        log.info("Saving a new user with email: {}", user.getEmail());

        // Verificar si el usuario ya existe
        usersRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new ExceptionMessage("El correo electrónico ya está registrado: " + user.getEmail());

                });

        // Establecer el usuario como habilitado
        user.setEnabled(true);
        // Codificar la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Guardar el nuevo usuario
        Users savedUser = usersRepository.save(user);

        log.info("Usuario creado con éxito: {}", savedUser.getId());

        // Construir la respuesta
        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario creado con éxito!");
        response.put("userId", String.valueOf(savedUser.getId()));

        return ResponseEntity.ok(response);
    }

    @Override
    public List<Users> getUsers() {
        log.info("Fetching all users");

        // Obtener la lista de usuarios
        List<Users> users = (List<Users>) usersRepository.findAll();

        // Validar si la lista está vacía
        if (users.isEmpty()) {
            log.warn("No users found in the database");
            throw new ExceptionMessage("No se encontraron usuarios registrados");
        }

        log.info("Total users found: {}", users.size());
        return users;
    }

    @Override
    public Users getUser(String email) {
        log.info("Fetching user with email: {}", email);

        return usersRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User with email {} not found", email);
                    return new ExceptionMessage("Usuario no registrado");
                });
    }


    @Override
    public ResponseEntity<Map<String, String>> updateUser(final Users user) {
        // Buscar el usuario existente por id
        var existingUser = usersRepository.findById(user.getId())
                .orElseThrow(() -> new ExceptionMessage("No se encontró un usuario con el email: " + user.getEmail()));

        // Actualizar los campos necesarios
        if (user.getName() != null && !user.getName().isBlank()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            existingUser.setEmail(user.getEmail());
        }
         // Guardar el usuario actualizado en la base de datos
        try {
            usersRepository.save(existingUser);
        } catch (Exception e) {
            throw new ExceptionMessage("Error al guardar el usuario actualizado: " + e.getMessage());
        }

        // Construir la respuesta
        Map<String, String> response = Map.of("message", "Usuario actualizado con éxito!");
        return ResponseEntity.ok(response);
    }


    @Override
    public Long countUsers() {
        return usersRepository.count();
    }

    @Override
    public Long countRoles() {
        return roleRepository.count();
    }


    @Override
    public Role saveRole(Role role) {
        log.info("Attempting to save a new role: {}", role.getName());

        if (roleRepository.findByName(role.getName()).isPresent()) {
            log.warn("Role with name {} already exists", role.getName());
            throw new ExceptionMessage("Rol ya registrado");
        }

        try {
            Role newRole = roleRepository.save(role);
            log.info("Role {} saved successfully", role.getName());
            return newRole;
        } catch (Exception e) {
            log.error("Error saving role {}: {}", role.getName(), e.getMessage());
            throw new ExceptionMessage("Ocurrió un error al guardar el rol: " + e.getMessage());
        }
    }


    @Override
    public void removeRoleFromUser(RoleAssignmentDTO removeRoleDto) {
        log.info("Attempting to remove role '{}' from user '{}'", removeRoleDto.getRoleName(), removeRoleDto.getEmail());

        // Buscar el usuario en el repositorio
        var userOptional = usersRepository.findByEmail(removeRoleDto.getEmail());
        if (userOptional.isEmpty()) {
            log.warn("User with email '{}' not found", removeRoleDto.getEmail());
            throw new ExceptionMessage("Usuario no registrado con el email: " + removeRoleDto.getEmail());
        }

        // Buscar el rol en el repositorio
        var roleOptional = roleRepository.findByName(removeRoleDto.getRoleName());
        if (roleOptional.isEmpty()) {
            log.warn("Role '{}' not found", removeRoleDto.getRoleName());
            throw new ExceptionMessage("Rol no registrado: " + removeRoleDto.getRoleName());
        }

        var user = userOptional.get();
        var role = roleOptional.get();

        // Verificar si el usuario tiene el rol antes de intentar removerlo
        if (!user.getRoles().contains(role)) {
            log.warn("User '{}' does not have the role '{}'", removeRoleDto.getEmail(), removeRoleDto.getRoleName());
            throw new ExceptionMessage("El usuario no tiene el rol: " + removeRoleDto.getRoleName());
        }

        // Remover el rol del usuario
        user.getRoles().remove(role);
        usersRepository.save(user);
        log.info("Role '{}' removed from user '{}'", removeRoleDto.getRoleName(), removeRoleDto.getEmail());
    }



    @Override
    public void addRoleToUser(RoleAssignmentDTO roleAssignmentDTO) {
        log.info("Adding role '{}' to user '{}'", roleAssignmentDTO.getRoleName(), roleAssignmentDTO.getEmail());

        // Buscar usuario por email
        var userOptional = usersRepository.findByEmail(roleAssignmentDTO.getEmail());
        if (userOptional.isEmpty()) {
            throw new ExceptionMessage("Usuario con email '" + roleAssignmentDTO.getEmail() + "' no encontrado");
        }

        // Buscar rol por nombre
        var roleOptional = roleRepository.findByName(roleAssignmentDTO.getRoleName());
        if (roleOptional.isEmpty()) {
            throw new ExceptionMessage("Rol con nombre '" + roleAssignmentDTO.getRoleName() + "' no encontrado");
        }

        Users user = userOptional.get();
        Role role = roleOptional.get();

        // Verificar si el usuario ya tiene el rol asignado
        if (user.getRoles().contains(role)) {
            throw new ExceptionMessage("El usuario ya tiene asignado el rol '" + roleAssignmentDTO.getRoleName() + "'");
        }

        // Asignar el rol y guardar
        user.getRoles().add(role);
        usersRepository.save(user);

        log.info("Role '{}' added to user '{}'", roleAssignmentDTO.getRoleName(), roleAssignmentDTO.getEmail());
    }


    @Override
    public ResponseEntity<Map<String, String>> changeUserStatus(UserStatusDto changeUserStatusDto) {
        // Buscar el usuario en el repositorio
        var optionalUser = usersRepository.findByEmail(changeUserStatusDto.getEmail());

        // Validar si el usuario fue encontrado
        if (optionalUser.isEmpty()) {
            throw new ExceptionMessage("No se encontró un usuario con el email: " + changeUserStatusDto.getEmail());
        }

        // Obtener el empleado del Optional
        var user = optionalUser.get();

        // Actualizar el estado del usuario
        user.setEnabled(changeUserStatusDto.getEnabled());

        try {
            usersRepository.save(user);
        } catch (Exception e) {
            throw new ExceptionMessage("Error al guardar el estado del usuario: " + e.getMessage());
        }

        // Crear y devolver la respuesta
        var responseBody = Map.of("message", "Estado del usuario modificado con éxito!");
        return ResponseEntity.ok(responseBody);
    }



    @Override
    public ResponseEntity<Map<String, String>> changePassword(UpdatePasswordDto updatePassword) {
        try {
            // Buscar el usuario en el repositorio
            var optionalUser = usersRepository.findByEmail(updatePassword.getEmail());

            // Validar si el usuario existe
            if (optionalUser.isEmpty()) {
                throw new ExceptionMessage("No se encontró un usuario con el email: " + updatePassword.getEmail());
            }

            var user = optionalUser.get();

            // Actualizar el password
            // Codificar la contraseña antes de guardar
            user.setPassword(passwordEncoder.encode(updatePassword.getNewPassword()));
            usersRepository.save(user);
        } catch (Exception e) {
            throw new ExceptionMessage("Error al actualizar el password del usuario: " + e.getMessage());
        }

        // Crear y devolver la respuesta
        var responseBody = Map.of("message", "Password actualizado con éxito!");
        return ResponseEntity.ok(responseBody);
    }



    @Override
    public UserDto loginUser(String email, String password) {
        var user = usersRepository.findByEmailAndPassword(email,password);
        if (user == null) {
            throw new IllegalArgumentException ("Usuario o contraseña incorrectos" );
        }
        try {
            UserDto userDto= new UserDto();
          /*  userDto.setName(user.getName());
            userDto.setRol(user.getRol().toString());*/
            return userDto;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }

    }
}
