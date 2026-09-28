package com.julian.cv.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.julian.cv.model.EmailSendEvent;

@Service
public class MailProducer {

    private static final Logger log =
            LoggerFactory.getLogger(MailProducer.class);

    private static final String TOPIC = "mail.send";

    private final KafkaTemplate<String, EmailSendEvent> kafkaTemplate;

    public MailProducer(KafkaTemplate<String, EmailSendEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEmail(EmailSendEvent event) {

        log.info("📤 Publicando evento de email en Kafka");
        log.info("📌 Topic: {}", TOPIC);
        log.info("📧 Destinatarios: {}", event.to());
        log.info("📝 Asunto: {}", event.subject());

        kafkaTemplate.send(TOPIC, event)
                .whenComplete((result, ex) -> {

                    if (ex != null) {
                        log.error(
                                "❌ Error publicando evento en Kafka - topic={}",
                                TOPIC,
                                ex
                        );
                        return;
                    }

                    log.info(
                            "✅ Evento publicado correctamente en Kafka - topic={}, partition={}, offset={}",
                            TOPIC,
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                });
    }
}