package com.microservices.sendemail.controller;

import com.microservices.sendemail.Cmd;
import com.microservices.sendemail.HttpResponse;
import com.microservices.sendemail.dtos.SendEmailDto;
import com.microservices.sendemail.models.EmailModel;
import com.microservices.sendemail.sendmessage.EmailSender;
import com.microservices.sendemail.services.EmailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.List;


@RestController
public class EmailController {

    @Autowired
    EmailService emailService;

    @Autowired
    EmailSender emailSender;
    @PostMapping("/send-email")
    public ResponseEntity<Object> sendEmail(@RequestBody SendEmailDto emailDto) {
        HttpResponse httpResponse = new HttpResponse<Object>();
        try {
            String[] requiredFields = {"emailTo", "subject", "text"};

            for (String field : requiredFields) {
                try {
                    Field declaredField = emailDto.getClass().getDeclaredField(field);
                    declaredField.setAccessible(true);
                    if (declaredField.get(emailDto) == null) {
                        httpResponse.setData("Missing param: " + field);
                        return new ResponseEntity<>(httpResponse, HttpStatus.BAD_REQUEST);
                    }
                } catch (NoSuchFieldException e) {
                    httpResponse.setData("Error: " + e.getMessage());
                    return new ResponseEntity<>(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            EmailModel emailModel = new EmailModel();
            BeanUtils.copyProperties(emailDto, emailModel);
            emailSender.send(emailModel);
            httpResponse.setData("Email was send!");
            return new ResponseEntity<>(httpResponse, HttpStatus.CREATED);
        }catch (Exception e){
            httpResponse.setData("Error: " + e.getMessage());
            return new ResponseEntity<>(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<Object> getEmails() {
        HttpResponse httpResponse = new HttpResponse<Object>();
        try {
            httpResponse.setData(emailService.getEmails());
            return new ResponseEntity<>(httpResponse, HttpStatus.OK);
        }catch(Exception e){
            httpResponse.setData("Error: " + e.getMessage());
            return new ResponseEntity<>(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/retry-emails")
    public ResponseEntity<Object> retryFailedEmails() {
        HttpResponse httpResponse = new HttpResponse<Object>();
        try {
            List<Long> emails = emailService.failedEmails();
            if(emails.isEmpty()){
                httpResponse.setData("All emails was send");
                return ResponseEntity.status(HttpStatus.OK).body(httpResponse);
            }
            Cmd cmd = new Cmd();
            cmd.setCmd(1);
            emailSender.retrySend(cmd);
            httpResponse.setData("Amount of emails that was retry: " + emails.size());
            return ResponseEntity.status(HttpStatus.OK).body(httpResponse);
        }catch (Exception e){
            httpResponse.setData("Error: " + e.getMessage());
            return new ResponseEntity<>(httpResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
