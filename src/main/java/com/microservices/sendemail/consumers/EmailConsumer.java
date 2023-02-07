package com.microservices.sendemail.consumers;

import com.microservices.sendemail.Cmd;

import com.microservices.sendemail.models.EmailModel;
import com.microservices.sendemail.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;



@Component
public class EmailConsumer {

    @Autowired
    EmailService emailService;

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void listen(@Payload EmailModel emailModel){
        emailService.sendEmail(emailModel);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void listen(@Payload Cmd cmd){
        if(cmd.getCmd() == 1){
            emailService.retryEmails();
        }
    }

}
