package com.linktune.api.Controller;

import com.linktune.api.Service.JwtService;
import com.linktune.api.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager; // Doğrudan buraya enjekte ediliyor.
    private final JwtService jwtService; // Doğrudan buraya enjekte ediliyor.

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@RequestBody Map<String, String> request) {
        userService.registerUser(request.get("username"), request.get("password"));
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> request) {
        // Login mantığı artık burada.
        String username = request.get("username");
        String password = request.get("password");

        // Önce kimliği doğrula
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        // Kimlik doğrulandıysa, token üret
        String token = jwtService.generateToken(username);

        Map<String, String> response = Map.of("token", token);
        return ResponseEntity.ok(response);
    }
}