package com.xxz.loginhouduan.controller;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import com.xxz.loginhouduan.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * Forgot Password - Send Password Reset Email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        SysUserEntity user = sysUserMapper.findByEmail(email);

        if (user == null) {
            return ResponseEntity.badRequest().body("This email is not registered.");
        }

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expireTime = LocalDateTime.now().plusHours(1); // Token valid for 1 hour
        sysUserMapper.updateResetToken(email, resetToken, expireTime.toString());

        // Send password reset email
        String resetLink = "http://localhost:8080/ResetPassword?token=" + resetToken;
        emailService.sendEmail(email, resetToken);

        return ResponseEntity.ok("A password reset link has been sent to your email.");
    }

    /**
     * Reset Password
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        // 查找用户
        SysUserEntity user = sysUserMapper.findByResetToken(token);
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid or expired reset link.");
        }

        // 获取并解析 token 过期时间
        LocalDateTime expireTime = user.getResetTokenExpire();

        // 检查 token 是否过期
        if (expireTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Reset link has expired. Please request a new one.");
        }

        // 使用 MD5 对新密码进行加密
        String encryptedPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());

        // 更新密码（加密后的密码）
        sysUserMapper.updatePassword(token, encryptedPassword);

        return ResponseEntity.ok("Your password has been successfully reset.");
    }


}