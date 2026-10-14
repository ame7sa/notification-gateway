package gateway;

import gateway.notification.Notification;
import gateway.notification.UrgentNotification;
import gateway.sender.EmailMessageSender;
import gateway.sender.TelegramMessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuntimeSwitchingTest {

    @Test
    @DisplayName("same notification object can switch from Telegram to Email")
    void switchProviderAtRuntime() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        Notification notification = new UrgentNotification(telegram);

        notification.send("user@mail.com", "first");
        notification.setSender(email);
        notification.send("user@mail.com", "second");

        assertEquals(1, telegram.getSentMessages().size());
        assertEquals(1, email.getSentMessages().size());
        assertSame(email, notification.getSender());
    }

    @Test
    @DisplayName("null sender is rejected")
    void nullSenderRejected() {
        Notification notification = new UrgentNotification(new TelegramMessageSender());

        assertThrows(NullPointerException.class, () -> notification.setSender(null));
    }
}
