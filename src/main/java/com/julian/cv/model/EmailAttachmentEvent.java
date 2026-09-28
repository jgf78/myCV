package com.julian.cv.model;

public record EmailAttachmentEvent(
        String filename,
        String contentType,
        long size,
        byte[] content
) {
}