package com.thuctap.bookmanage.repository;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailSender {
    void send(String to, String email) throws IllegalAccessException, MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException;
}
