package gateway.sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common stuff for the fake providers: availability flag, an "outbox" so we
 * can check what was sent, and the basic checks every channel needs.
 * Each real sender only decides how the payload looks and what it rejects.
 */
public abstract class SimulatedSender implements MessageSender {

    private final String channelName;
    private final List<String> sentMessages = new ArrayList<>();
    private boolean available = true;

    protected SimulatedSender(String channelName) {
        this.channelName = channelName;
    }

    @Override
    public final boolean sendMessage(String recipient, String title, String body) {
        if (!available) {
            return false;
        }
        if (recipient == null || recipient.isBlank()) {
            return false;
        }

        String safeTitle = title == null ? "" : title;
        String safeBody = body == null ? "" : body;

        String payload = buildPayload(recipient, safeTitle, safeBody);
        if (payload == null) {
            // the channel refused this message
            return false;
        }

        sentMessages.add(payload);
        System.out.println("  [" + channelName + "] " + payload.replace("\n", "\n      "));
        return true;
    }

    /** Returns the final text for this channel, or null if the channel can't send it. */
    protected abstract String buildPayload(String recipient, String title, String body);

    @Override
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    public List<String> getSentMessages() {
        return Collections.unmodifiableList(sentMessages);
    }

    public String getLastMessage() {
        return sentMessages.isEmpty() ? null : sentMessages.get(sentMessages.size() - 1);
    }
}
