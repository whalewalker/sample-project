package com.sample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getNotifications() {
        return notificationRepository.findAll();
    }

    public Notification createNotification(String message) {
        Notification notification = new Notification(message);
       return notificationRepository.save(notification);
    }

    public Notification getNotification(String id){
        Notification notification = notificationRepository.findById(id).get();
        notification.setRead(true);
        return notificationRepository.save(notification);
    }
}
