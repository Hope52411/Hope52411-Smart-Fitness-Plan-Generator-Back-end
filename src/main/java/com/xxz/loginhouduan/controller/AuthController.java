package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EmailService emailService;

    // Frontend domain for password reset link, configured in application.yml
    @Value("${app.frontend-domain}")
    private String frontendDomain;

    /**
     * Forgot Password - Send password reset email
     * @param request Request body containing user's email
     * @return Response message
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        SysUserEntity user = sysUserMapper.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().body("This email is not registered.");
        }

        // Generate a unique reset token and expiration time (valid for 1 hour)
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(1);

        // Update user's reset token and expiration time in database
        sysUserMapper.updateResetToken(email, resetToken, expireTime.toString());

        // Build the password reset link
        String resetLink = frontendDomain + "/reset-password?token=" + resetToken;

        // Send password reset email
        emailService.sendEmail(email, resetToken);

        return ResponseEntity.ok("A password reset link has been sent to your email.");
    }

    /**
     * Reset Password
     * @param request Request body containing reset token and new password
     * @return Response message
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        // Find user by reset token
        SysUserEntity user = sysUserMapper.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid or expired reset link.");
        }

        // Get token expiration time
        LocalDateTime expireTime = user.getResetTokenExpire();

        // Check if token has expired
        if (expireTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Reset link has expired. Please request a new one.");
        }

        // Encrypt the new password using MD5
        String encryptedPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());

        // Update user's password in database
        sysUserMapper.updatePassword(token, encryptedPassword);

        return ResponseEntity.ok("Your password has been successfully reset.");
    }
}
