package com.MohammadMarediya.controllers;

import com.MohammadMarediya.auth.entities.ForgotPassword;
import com.MohammadMarediya.auth.entities.User;
import com.MohammadMarediya.auth.repositories.ForgotPasswordRepository;
import com.MohammadMarediya.auth.repositories.UserRepository;
import com.MohammadMarediya.auth.utils.ChangePassword;
import com.MohammadMarediya.dto.MailBody;
import com.MohammadMarediya.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/forgotPassword")
@CrossOrigin(origins = "*")
@Tag(name = "Forgot Password Controller", description = "APIs for handling Forgot Password functionality")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository, EmailService emailService, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
            summary = "Send OTP to user's email",
            description = "Sends an OTP to the user's registered email address for password reset.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email sent for verification"),
                    @ApiResponse(responseCode = "404", description = "Email not found", content = @Content)
            }
    )
    @PostMapping("/verifyMail/{email}")
    public ResponseEntity<String> verifyEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email: " + email));

        int otp = otpGenerator();
        MailBody mailBody = MailBody.builder()
                .to(email)
                .text("This is the OTP for your Forgot Password request: " + otp)
                .subject("OTP for Forgot Password request")
                .build();

        ForgotPassword fp = ForgotPassword.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + 20 * 1000 * 60)) // 20 minutes
                .user(user)
                .build();

        emailService.sendSimpleMessage(mailBody);
        forgotPasswordRepository.save(fp);

        return ResponseEntity.ok("Email sent for verification!");
    }

    @Operation(
            summary = "Verify OTP",
            description = "Validates the OTP sent to the user's email.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
                    @ApiResponse(responseCode = "417", description = "OTP expired", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid OTP or email", content = @Content)
            }
    )
    @PostMapping("/verifyOtp/{otp}/{email}")
    public ResponseEntity<String> verifyOtp(@PathVariable Integer otp, @PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Please provide a valid email!"));

        ForgotPassword fp = forgotPasswordRepository.findByOtpAndUser(otp, user)
                .orElseThrow(() -> new RuntimeException("Invalid OTP for email: " + email));

        if (fp.getExpirationTime().before(Date.from(Instant.now()))) {
            forgotPasswordRepository.deleteById(fp.getFpid());
            return new ResponseEntity<>("OTP has expired!", HttpStatus.EXPECTATION_FAILED);
        }

        return ResponseEntity.ok("OTP verified!");
    }

    @Operation(
            summary = "Change Password",
            description = "Changes the user's password after verifying OTP.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ChangePassword.class)
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Password has been changed"),
                    @ApiResponse(responseCode = "417", description = "Passwords do not match", content = @Content)
            }
    )
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(
            @org.springframework.web.bind.annotation.RequestBody ChangePassword changePassword,
            @PathVariable String email) {

        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }

        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);

        return ResponseEntity.ok("Password has been changed!");
    }

    private Integer otpGenerator() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
