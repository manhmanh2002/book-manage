package com.thuctap.bookmanage.service;

import com.thuctap.bookmanage.repository.EmailSender;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;


@Service
@AllArgsConstructor
public class EmailService implements EmailSender {
    @Autowired
    private final JavaMailSender mailSender;
    @Override
    @Async
    public void send(String to, String email) throws MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");

        helper.setFrom("contact@test.com","Support");
        helper.setTo(to);

        String subject = "Here the link: ";
        helper.setSubject(subject);
        helper.setText(email,true);

        mailSender.send(message);

    }


}
