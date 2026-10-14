package gateway.notification;

import gateway.sender.MessageSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrgentNotification extends Notification {

    private static final String PREFIX = "[URGENT] ";
    private static final String TITLE = "Urgent";

    private final List<MessageSender> fallbacks = new ArrayList<>();
    private String lastUsedChannel;

    public UrgentNotification(MessageSender primary, MessageSender... fallbacks) {
        super(primary);
        this.fallbacks.addAll(Arrays.asList(fallbacks));
    }

    public void addFallback(MessageSender fallback) {
        fallbacks.add(fallback);
    }

    @Override
    public void send(String recipient, String message) {
        String body = PREFIX + message;

        // primary first, then the fallbacks in the order they were added
        List<MessageSender> chain = new ArrayList<>();
        chain.add(sender);
        chain.addAll(fallbacks);

        for (MessageSender candidate : chain) {
            if (!candidate.isAvailable()) {
                System.out.println("  " + candidate.getChannelName() + " is unavailable, trying the next one");
                continue;
            }
            if (candidate.sendMessage(recipient, TITLE, body)) {
                lastUsedChannel = candidate.getChannelName();
                return;
            }
            System.out.println("  " + candidate.getChannelName() + " refused the message, trying the next one");
        }

        lastUsedChannel = null;
        throw new DeliveryFailedException("urgent message to " + recipient + " was not delivered by any channel");
    }

    /** Name of the channel that actually delivered the last message, null if it failed. */
    public String getLastUsedChannel() {
        return lastUsedChannel;
    }
}
