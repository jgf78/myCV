package com.julian.cv.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.julian.cv.model.EmailSendEvent;

@Service
public class MailProducer {

    private static final String TOPIC = "mail.send";

    private final KafkaTemplate<String, EmailSendEvent> kafkaTemplate;

    public MailProducer(KafkaTemplate<String, EmailSendEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(EmailSendEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}