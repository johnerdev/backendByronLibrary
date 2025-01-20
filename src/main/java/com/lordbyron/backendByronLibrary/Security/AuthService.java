package com.lordbyron.backendByronLibrary.Security;

import com.lordbyron.backendByronLibrary.Dto.userDto.LoginDto;
import com.lordbyron.backendByronLibrary.entity.Users;
import com.lordbyron.backendByronLibrary.exception.ExceptionMessage;
import com.lordbyron.backendByronLibrary.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String login(LoginDto loginDto) {
        Users user = usersRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ExceptionMessage("Usuario no encontrado"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new ExceptionMessage("Contraseña incorrecta");
        }

        return jwtTokenUtil.generateToken(user);
    }
}
