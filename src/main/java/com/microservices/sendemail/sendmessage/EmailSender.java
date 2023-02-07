package com.microservices.sendemail.sendmessage;
import com.microservices.sendemail.Cmd;

import com.microservices.sendemail.models.EmailModel;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class EmailSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue queue;

    public void send(EmailModel emailModel) {
        rabbitTemplate.convertAndSend(this.queue.getName(), emailModel);
    }

    public void retrySend(Cmd cmd) {
        rabbitTemplate.convertAndSend(this.queue.getName(), cmd);
    }

}
