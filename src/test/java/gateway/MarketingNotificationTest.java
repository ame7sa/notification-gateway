package gateway;

import gateway.notification.DeliveryFailedException;
import gateway.notification.MarketingNotification;
import gateway.notification.Notification;
import gateway.sender.EmailMessageSender;
import gateway.sender.KafkaEventSender;
import gateway.sender.PushNotificationSender;
import gateway.sender.TelegramMessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Marketing was added later, none of the senders were touched for it. */
class MarketingNotificationTest {

    @Test
    @DisplayName("marketing notification works with the push sender")
    void worksWithPush() {
        PushNotificationSender push = new PushNotificationSender();
        Notification notification = new MarketingNotification(push, "AUTUMN");

        notification.send("device-1", "20% off");

        assertTrue(push.getLastMessage().contains("[AUTUMN] 20% off"));
        assertTrue(push.getLastMessage().contains("Reply STOP"));
    }

    @Test
    @DisplayName("marketing notification works with every old sender")
    void worksWithOldSenders() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        KafkaEventSender kafka = new KafkaEventSender();

        new MarketingNotification(telegram).send("@user", "sale");
        new MarketingNotification(email).send("a@b.com", "sale");
        new MarketingNotification(kafka).send("user-1", "sale");

        assertEquals(1, telegram.getSentMessages().size());
        assertEquals(1, email.getSentMessages().size());
        assertEquals(1, kafka.getSentMessages().size());
    }

    @Test
    @DisplayName("marketing notification fails if its provider is down")
    void failsWhenProviderDown() {
        PushNotificationSender push = new PushNotificationSender();
        push.setAvailable(false);
        Notification notification = new MarketingNotification(push);

        assertThrows(DeliveryFailedException.class,
                () -> notification.send("device-1", "sale"));
    }
}
