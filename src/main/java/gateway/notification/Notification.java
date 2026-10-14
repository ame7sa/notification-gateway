package gateway.notification;

import gateway.sender.MessageSender;

import java.util.Objects;

/**
 * Abstraction in the Bridge pattern. The "bridge" is the sender field:
 * every notification type holds a MessageSender and talks to it only
 * through the interface.
 */
public abstract class Notification {

    protected MessageSender sender;

    protected Notification(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
    }

    public void setSender(MessageSender sender) {
        this.sender = Objects.requireNonNull(sender, "sender must not be null");
    }

    public MessageSender getSender() {
        return sender;
    }

    public abstract void send(
            String recipient,
            String message
    );

    /** Plain "send once with the current sender", throws if it didn't work. */
    protected void deliver(String recipient, String title, String body) {
        if (!sender.isAvailable()) {
            throw new DeliveryFailedException(sender.getChannelName() + " is not available");
        }
        if (!sender.sendMessage(recipient, title, body)) {
            throw new DeliveryFailedException(
                    sender.getChannelName() + " rejected the message for " + recipient);
        }
    }
}
