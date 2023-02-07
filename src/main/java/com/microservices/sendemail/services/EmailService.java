package com.microservices.sendemail.services;


import com.microservices.sendemail.enums.StatusEmail;
import com.microservices.sendemail.models.EmailModel;
import com.microservices.sendemail.repositories.EmailRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class EmailService {

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    private JavaMailSender emailSender;

    @Transactional
    public EmailModel sendEmail(EmailModel emailModel) {
        emailModel.setEmailSentOn(LocalDateTime.now());
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailModel.getEmailFrom());
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getSubject());
            message.setText(emailModel.getText());
            emailModel.setEmailFrom("libertyisrequired@gmail.com");
            emailSender.send(message);

            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (MailException e){
            System.out.println(e);
            emailModel.setStatusEmail(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel);
            return emailModel;
        }
    }

    public List<EmailModel> getEmails(){
        return emailRepository.findAll();
    }

    public Optional<EmailModel> getEmailById(Long id){
        return emailRepository.findById(id);
    }

    public List<Long> failedEmails() {
        List<Long> failedEmails = emailRepository.getIdFailedEmails();
        return failedEmails;
    }

    public void retryEmail(Long id) {
        Optional<EmailModel> emailModel = this.getEmailById(id);
        if(emailModel != null){
            this.sendEmail(emailModel.get());
        }
    }

    public void retryEmails() {
        List<EmailModel> emails = emailRepository.getFailedEmails();
        for(int i = 0; i<emails.size(); ++i){
            this.resendEmails(emails.get(i));
        }
    }

    public void resendEmails(EmailModel emailModel){
        emailRepository.deleteById(emailModel.getId());
        emailModel.setEmailSentOn(LocalDateTime.now());
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailModel.getEmailFrom());
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getSubject());
            message.setText(emailModel.getText());
            emailModel.setEmailFrom("libertyisrequired@gmail.com");
            emailSender.send(message);

            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (MailException e){
            System.out.println(e);
            emailModel.setStatusEmail(StatusEmail.ERROR);
        } finally {
            emailRepository.save(emailModel);
        }
    }
}
