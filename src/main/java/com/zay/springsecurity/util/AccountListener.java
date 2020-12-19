package com.zay.springsecurity.util;

import com.zay.springsecurity.model.User;
import com.zay.springsecurity.service.UserService;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AccountListener implements ApplicationListener<OnCreateAccountEvent> {

    private final String serverUrl = "http://localhost:8080/";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    @Autowired
    private VelocityEngine velocityEngine;

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
        String subject = "Account Confirmation for your Application";
        String confirmationUrl = event.getAppUrl() + "/accountConfirm?token=" +token;


        Map<String, Object> model = new HashMap<String, Object>();
        model.put("verification", serverUrl+confirmationUrl);
        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "verification.vm", "UTF-8", model);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
                helper = new MimeMessageHelper(mimeMessage, true);
                helper.setTo(recipientAddress);
                helper.setSubject(subject);
                helper.setSentDate(new Date());
                helper.setText(text,true);

//                FileSystemResource file = new FileSystemResource(new File("C:/Users/ZayarHtet/Downloads/Tatkatho Design and Theme/V2.png"));
//                helper.addInline("logo", file);
        } catch (MessagingException e) {
                e.printStackTrace();
        }
        
        mailSender.send(mimeMessage);
    }
}
