package com.sample;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notifications")
@Data
public class Notification {
    @Id
    private String id;
    private String message;
    private boolean isRead;

    public Notification(String message) {
        this.message = message;
    }
}
