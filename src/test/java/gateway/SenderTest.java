package gateway;

import gateway.sender.EmailMessageSender;
import gateway.sender.KafkaEventSender;
import gateway.sender.SmsMessageSender;
import gateway.sender.TelegramMessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SenderTest {

    @Test
    @DisplayName("telegram sends the title in bold")
    void telegramUsesFormatting() {
        TelegramMessageSender telegram = new TelegramMessageSender();

        boolean ok = telegram.sendMessage("@user", "Hello", "body text");

        assertTrue(ok);
        assertTrue(telegram.getLastMessage().contains("*Hello*"));
        assertTrue(telegram.getLastMessage().contains("body text"));
    }

    @Test
    @DisplayName("email has a subject and a signature, and needs a real address")
    void emailHasTitleAndSignature() {
        EmailMessageSender email = new EmailMessageSender();

        assertTrue(email.sendMessage("a@b.com", "Report", "see attachment"));
        assertTrue(email.getLastMessage().contains("Subject: Report"));
        assertTrue(email.getLastMessage().contains("Notification Gateway Team"));

        assertFalse(email.sendMessage("no-at-sign", "Report", "x"));
        assertEquals(1, email.getSentMessages().size());
    }

    @Test
    @DisplayName("sms accepts exactly 160 characters")
    void smsAcceptsLimit() {
        SmsMessageSender sms = new SmsMessageSender();
        String text = "a".repeat(160);

        assertTrue(sms.sendMessage("+77011234567", "", text));
    }

    @Test
    @DisplayName("sms rejects a message longer than 160 characters")
    void smsRejectsTooLongMessage() {
        SmsMessageSender sms = new SmsMessageSender();
        String text = "a".repeat(161);

        assertFalse(sms.sendMessage("+77011234567", "", text));
        assertTrue(sms.getSentMessages().isEmpty());
    }

    @Test
    @DisplayName("sms limit can be changed to 120")
    void smsLimitIsConfigurable() {
        SmsMessageSender sms = new SmsMessageSender(120);

        assertTrue(sms.sendMessage("+77011234567", "", "a".repeat(120)));
        assertFalse(sms.sendMessage("+77011234567", "", "a".repeat(121)));
    }

    @Test
    @DisplayName("sms refuses something that is not a phone number")
    void smsChecksPhoneNumber() {
        SmsMessageSender sms = new SmsMessageSender();

        assertFalse(sms.sendMessage("john", "", "hi"));
    }

    @Test
    @DisplayName("kafka publishes events with growing offsets")
    void kafkaPublishesEvents() {
        KafkaEventSender kafka = new KafkaEventSender("alerts");

        assertTrue(kafka.sendMessage("user-1", "T", "first"));
        assertTrue(kafka.sendMessage("user-1", "T", "second"));

        assertTrue(kafka.getSentMessages().get(0).contains("offset=0"));
        assertTrue(kafka.getSentMessages().get(1).contains("offset=1"));
        assertTrue(kafka.getLastMessage().contains("topic=alerts"));
    }

    @Test
    @DisplayName("unavailable sender does not send anything")
    void unavailableSenderSendsNothing() {
        TelegramMessageSender telegram = new TelegramMessageSender();
        telegram.setAvailable(false);

        assertFalse(telegram.isAvailable());
        assertFalse(telegram.sendMessage("@user", "T", "B"));
        assertTrue(telegram.getSentMessages().isEmpty());
    }
}
