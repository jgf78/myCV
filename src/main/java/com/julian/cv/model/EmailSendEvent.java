package com.julian.cv.model;

import java.util.List;

public record EmailSendEvent(
        List<String> to,
        List<String> cc,
        List<String> bcc,
        String replyTo,
        String subject,
        String body,
        boolean html,
        List<EmailAttachmentEvent> attachments
) {
}