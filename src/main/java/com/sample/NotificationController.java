package com.sample;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private volatile String lastNotificationId;
    AtomicInteger atomicInteger = new AtomicInteger(0);

    @GetMapping("/notifications")
    public Flux<ServerSentEvent<List<Notification>>> getNotifications() {
        return Flux.interval(Duration.ofSeconds(1)) // Emit a notification every second
                .flatMap(sequence -> {
                    List<Notification> currentNotifications = notificationService.getNotifications();
                    String currentNotificationId = currentNotifications.get(currentNotifications.size() - 1).getId();
                    // Check if the current notification list is different from the previous one
                    if (!currentNotificationId.equals(lastNotificationId)) {
                        lastNotificationId = currentNotificationId; // Update lastNotifications with the new list

                        return Flux.just(ServerSentEvent.<List<Notification>>builder()
                                .id(String.valueOf(sequence))
                                .event("notifications")
                                .data(currentNotifications)
                                .build());
                    } else {
                        return Flux.empty(); // Don't send a response if the notification list hasn't changed
                    }
                })
                .doOnCancel(() -> log.info("Client disconnected")); // Log when client disconnects
    }

    @GetMapping("/notifications/{id}")
    public Mono<Notification> getNotification(@PathVariable String id){
        return Mono.just(notificationService.getNotification(id));
    }


    @PostMapping("/notifications")
    public Mono<Notification> createNotification() {
        Notification notification = notificationService.createNotification("New notification " + atomicInteger.incrementAndGet());
        return Mono.just(notification);
    }
}