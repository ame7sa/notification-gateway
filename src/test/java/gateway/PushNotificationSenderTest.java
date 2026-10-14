package gateway;

import gateway.notification.EncryptedNotification;
import gateway.notification.Notification;
import gateway.notification.ScheduledBatchNotification;
import gateway.notification.UrgentNotification;
import gateway.sender.PushNotificationSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Push sender was added later, the existing notification classes were not touched. */
class PushNotificationSenderTest {

    @Test
    @DisplayName("push sender works with the existing urgent notification")
    void worksWithUrgent() {
        PushNotificationSender push = new PushNotificationSender();
        Notification notification = new UrgentNotification(push);

        notification.send("device-1", "build failed");

        assertTrue(push.getLastMessage().contains("[URGENT] build failed"));
    }

    @Test
    @DisplayName("push sender works with the existing encrypted notification")
    void worksWithEncrypted() {
        PushNotificationSender push = new PushNotificationSender();
        Notification notification = new EncryptedNotification(push);

        notification.send("device-1", "my secret");

        assertEquals(1, push.getSentMessages().size());
        assertFalse(push.getLastMessage().contains("my secret"));
    }

    @Test
    @DisplayName("push sender works with the existing batch notification")
    void worksWithBatch() {
        PushNotificationSender push = new PushNotificationSender();
        ScheduledBatchNotification batch =
                new ScheduledBatchNotification(push, List.of("device-1", "device-2", "device-3"));

        batch.sendToAll("hi");

        assertEquals(3, batch.getLastSummary().successful());
    }

    @Test
    @DisplayName("push cuts a long body")
    void longBodyIsShortened() {
        PushNotificationSender push = new PushNotificationSender();

        push.sendMessage("device-1", "T", "x".repeat(300));

        assertTrue(push.getLastMessage().endsWith("..."));
        assertTrue(push.getLastMessage().length() < 200);
    }
}
