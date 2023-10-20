package com.web.ecommerce.service;

import com.web.ecommerce.model.user.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class MailSenderService {

    private final JavaMailSender mailSender;

    public MailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(User user,String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Reset Your Password";
        String sender = "Ecommerce";
        String mailContent = "<h2>WE'VE RECEIVED A REQUEST TO RESET YOUR PASSWORD</h2>" +
                "<p>Hello, " + user.getFirstname() + " " + user.getLastname() + "</p>" +
                "<p>If you requested your password to be reset, please follow the following link to complete the action. <a href=\"" + url + "\">" + url + "</a></p>" +
                "<p>Please note that this link will expire 30 minutes after receipt of this email. If you are unable to reset your password within this time, please request for your password to be reset again</p" +
                "<br>" +
                "<p>ECommerce</p>";
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message,"UTF-8");
        messageHelper.setFrom("ecommerceApplication@gmail.com",sender);
        messageHelper.setTo(user.getEmail());
        message.setSubject(subject);
        message.setContent(mailContent,"text/html");
        mailSender.send(message);
    }

}
