package gateway;

import gateway.notification.DeliveryFailedException;
import gateway.notification.UrgentNotification;
import gateway.sender.EmailMessageSender;
import gateway.sender.SmsMessageSender;
import gateway.sender.TelegramMessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UrgentNotificationTest {

    @Test
    @DisplayName("urgent message gets the [URGENT] prefix and goes through the primary sender")
    void urgentAddsPrefix() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        UrgentNotification notification = new UrgentNotification(telegram);

        notification.send("@user", "disk is full");

        assertEquals(1, telegram.getSentMessages().size());
        assertTrue(telegram.getLastMessage().contains("[URGENT] disk is full"));
        assertEquals("Telegram", notification.getLastUsedChannel());
    }

    @Test
    @DisplayName("falls back to SMS when Telegram is unavailable")
    void fallbackWhenPrimaryUnavailable() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        SmsMessageSender sms = new SmsMessageSender();
        telegram.setAvailable(false);
        UrgentNotification notification = new UrgentNotification(telegram, sms);

        notification.send("+77011234567", "db down");

        assertTrue(telegram.getSentMessages().isEmpty());
        assertEquals(1, sms.getSentMessages().size());
        assertEquals("SMS", notification.getLastUsedChannel());
    }

    @Test
    @DisplayName("fallback only relies on the MessageSender interface")
    void fallbackWorksWithAnyImplementation() {
        FakeSender primary = new FakeSender("Primary", false);
        FakeSender backup = new FakeSender("Backup", true);
        UrgentNotification notification = new UrgentNotification(primary, backup);

        notification.send("anyone", "ping");

        assertTrue(primary.bodies.isEmpty());
        assertEquals("[URGENT] ping", backup.bodies.get(0));
        assertEquals("Backup", notification.getLastUsedChannel());
    }

    @Test
    @DisplayName("falls back when the primary is available but refuses the message")
    void fallbackWhenPrimaryRejects() {
        // sms is up, but "@user" is not a phone number so it refuses
        SmsMessageSender sms = new SmsMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        UrgentNotification notification = new UrgentNotification(sms, email);

        notification.send("user@mail.com", "check this");

        assertTrue(sms.getSentMessages().isEmpty());
        assertEquals(1, email.getSentMessages().size());
    }

    @Test
    @DisplayName("fallbacks are tried in the order they were added")
    void secondFallbackIsUsed() {
        FakeSender primary = new FakeSender("A", false);
        FakeSender first = new FakeSender("B", false);
        FakeSender second = new FakeSender("C", true);
        UrgentNotification notification = new UrgentNotification(primary, first);
        notification.addFallback(second);

        notification.send("x", "msg");

        assertEquals("C", notification.getLastUsedChannel());
    }

    @Test
    @DisplayName("throws when nobody can deliver the urgent message")
    void failsWhenEverythingIsDown() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        SmsMessageSender sms = new SmsMessageSender();
        telegram.setAvailable(false);
        sms.setAvailable(false);
        UrgentNotification notification = new UrgentNotification(telegram, sms);

        assertThrows(DeliveryFailedException.class,
                () -> notification.send("+77011234567", "help"));
        assertNull(notification.getLastUsedChannel());
    }
}
