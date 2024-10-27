package com.jacpower.groupsApp.ruleEngine.service;

import com.jacpower.groupsApp.model.Meeting;
import com.jacpower.groupsApp.utility.Constants;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender javaMailSender;
    private static final Logger logger= LoggerFactory.getLogger(EmailSenderService.class);

    public void sendWelcomeEmail(String toEmail, String firstname, String groupName, String username, String password){
        String subject="Welcome " +firstname+ " to "+ groupName;
        String body="Your new member account has been created under the username " +username+ " and password " +password +". Please go to http://localhost:5173/login and " +
                "login to change your password";
        try {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom("mutuginick@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);

            javaMailSender.send(message);
        }
        catch (Exception e){
            logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
            throw e;
        }
    }
    public void sendMeetingDetails(List<JsonObject> toEmails, Meeting meeting){
        String subject="New Meeting added";
        String body="New Meeting has been added with the following details: location: "+ meeting.location()+ " appearance: " +meeting.appearance()+ " objective: " + meeting.objective()+ " on" +
                "date "+ meeting.meetingDate()+ ".Please plan to attend" ;

        for (JsonObject jsonObject: toEmails){
            String email= jsonObject.getString("email");
            try {
                SimpleMailMessage message=new SimpleMailMessage();
                message.setFrom("mutuginick@gmail.com");
                message.setTo(email);
                message.setText(body);
                message.setSubject(subject);

                javaMailSender.send(message);
            }
            catch (Exception e){
                logger.error(Constants.ERROR_LOG_TEMPLATE, e.getMessage());
                throw e;
            }
        }



    }


}
