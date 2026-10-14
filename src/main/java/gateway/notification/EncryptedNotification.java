package gateway.notification;

import gateway.crypto.SimpleCipher;
import gateway.sender.MessageSender;

public class EncryptedNotification extends Notification {

    private static final String DEFAULT_KEY = "bridge-key";
    private static final String TITLE = "Encrypted message";

    private final SimpleCipher cipher;

    public EncryptedNotification(MessageSender sender) {
        this(sender, DEFAULT_KEY);
    }

    public EncryptedNotification(MessageSender sender, String key) {
        super(sender);
        this.cipher = new SimpleCipher(key);
    }

    @Override
    public void send(String recipient, String message) {
        deliver(recipient, TITLE, cipher.encrypt(message));
    }

    public String decrypt(String encrypted) {
        return cipher.decrypt(encrypted);
    }
}
