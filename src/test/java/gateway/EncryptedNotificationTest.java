package gateway;

import gateway.notification.DeliveryFailedException;
import gateway.notification.EncryptedNotification;
import gateway.sender.EmailMessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EncryptedNotificationTest {

    @Test
    @DisplayName("the sender gets an encrypted text, not the original")
    void messageIsEncrypted() {
        EmailMessageSender email = new EmailMessageSender();
        EncryptedNotification notification = new EncryptedNotification(email);

        notification.send("a@b.com", "secret grade 95");

        assertEquals(1, email.getSentMessages().size());
        assertFalse(email.getLastMessage().contains("secret grade 95"));
    }

    @Test
    @DisplayName("what the sender received can be decrypted back to the original")
    void roundTrip() {
        FakeSender fake = new FakeSender("Fake", true);
        EncryptedNotification notification = new EncryptedNotification(fake, "my-key");
        String original = "Привет, this is a test 123";

        notification.send("anyone", original);

        String received = fake.bodies.get(0);
        assertFalse(received.equals(original));
        assertEquals(original, notification.decrypt(received));
    }

    @Test
    @DisplayName("fails with an exception if the provider is unavailable")
    void failsWhenProviderIsDown() {
        EmailMessageSender email = new EmailMessageSender();
        email.setAvailable(false);
        EncryptedNotification notification = new EncryptedNotification(email);

        assertThrows(DeliveryFailedException.class,
                () -> notification.send("a@b.com", "text"));
    }

    @Test
    @DisplayName("fails if the provider rejects the recipient")
    void failsOnBadRecipient() {
        EncryptedNotification notification = new EncryptedNotification(new EmailMessageSender());

        assertThrows(DeliveryFailedException.class,
                () -> notification.send("not-an-email", "text"));
    }
}
