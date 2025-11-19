package com.example.datn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendNewPassword(String toEmail, String newPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("Đặt lại mật khẩu - PL Jacket");
            message.setText(
                    "Xin chào,\n\n" +
                            "Bạn đã yêu cầu đặt lại mật khẩu.\n\n" +
                            "Mật khẩu mới của bạn là: " + newPassword + "\n\n" +
                            "Vui lòng đăng nhập và đổi mật khẩu ngay sau khi nhận được email này.\n\n" +
                            "Trân trọng,\n" +
                            "PL Jacket Team"
            );

            mailSender.send(message);
            System.out.println("✅ Email đã được gửi tới: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi email: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        }
    }
}