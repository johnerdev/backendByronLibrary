package com.lordbyron.backendByronLibrary.Security;

import com.lordbyron.backendByronLibrary.Dto.userDto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String token = authService.login(loginDto);
        return ResponseEntity.ok(Map.of("token", token));
    }
}

