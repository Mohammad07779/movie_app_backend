package com.MohammadMarediya.controllers;

import com.MohammadMarediya.auth.entities.RefreshToken;
import com.MohammadMarediya.auth.entities.User;
import com.MohammadMarediya.auth.services.AuthService;
import com.MohammadMarediya.auth.services.JwtService;
import com.MohammadMarediya.auth.services.RefreshTokenService;
import com.MohammadMarediya.auth.utils.AuthResponse;
import com.MohammadMarediya.auth.utils.LoginRequest;
import com.MohammadMarediya.auth.utils.RefreshTokenRequest;
import com.MohammadMarediya.auth.utils.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication Controller", description = "APIs for user registration, login, and token refresh")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Register new user",
            description = "Registers a new user and returns authentication tokens.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid registration details", content = @Content)
            }
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@org.springframework.web.bind.annotation.RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }



    @Operation(
            summary = "User login",
            description = "Authenticates user and returns authentication tokens.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User logged in successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }



    @Operation(
            summary = "Refresh JWT access token",
            description = "Generates a new JWT access token using a valid refresh token.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token", content = @Content)
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@org.springframework.web.bind.annotation.RequestBody RefreshTokenRequest refreshTokenRequest) {

        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.getRefreshToken());
        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build());
    }
}
