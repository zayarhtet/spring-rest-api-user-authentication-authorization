package com.zay.springsecurity.util;

import com.zay.springsecurity.model.User;
import com.zay.springsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.UUID;

@Component
public class AccountListener implements ApplicationListener<OnCreateAccountEvent> {

    private final String serverUrl = "http://localhost:8080/";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(OnCreateAccountEvent event) {
        this.confirmCreateAccount(event);
    }

    private void confirmCreateAccount(OnCreateAccountEvent event) {
        //get the account
        //create  verification token

        User user = event.getUser();

        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user,token);

        //get email properties
        String recipientAddress = user.getEmail();
        String subject = "Account Confirmation for your Tatkatho";
        String confirmationUrl = event.getAppUrl() + "/accountConfirm?token=" +token;
        String message = "Please Confirm";

        //send our emails
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(recipientAddress);
//        email.setSubject(subject);
//        email.setText(message + "\r\n" +serverUrl+confirmationUrl);
//        mailSender.send(email);

        String html = "<i>Greetings!</i><br>";
        html += "<a href='"+serverUrl+confirmationUrl+"'>Click here to verify your account</a><br>";
        html += "<font color=red>Duke</font>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
                helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(recipientAddress);
                helper.setSubject(subject);
                helper.setSentDate(new Date());
                helper.setText(html,true);
        } catch (MessagingException e) {
                e.printStackTrace();
        }
        
        mailSender.send(mimeMessage);
    }
}
