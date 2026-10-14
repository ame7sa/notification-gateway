package gateway.notification;

import gateway.sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

public class ScheduledBatchNotification extends Notification {

    private static final String TITLE = "Scheduled notification";

    private final List<String> recipients;
    private BatchSummary lastSummary;

    public ScheduledBatchNotification(MessageSender sender, List<String> recipients) {
        super(sender);
        if (recipients == null || recipients.isEmpty()) {
            throw new IllegalArgumentException("batch needs at least one recipient");
        }
        this.recipients = new ArrayList<>(recipients);
    }

    /**
     * Sends to everyone in the batch. The recipient argument comes from the
     * base class, here it is just an optional extra person on top of the list.
     */
    @Override
    public void send(String recipient, String message) {
        List<String> all = new ArrayList<>(recipients);
        if (recipient != null && !recipient.isBlank() && !all.contains(recipient)) {
            all.add(recipient);
        }

        int ok = 0;
        for (String r : all) {
            if (sender.isAvailable() && sender.sendMessage(r, TITLE, message)) {
                ok++;
            }
        }
        lastSummary = new BatchSummary(all.size(), ok, all.size() - ok);
    }

    public void sendToAll(String message) {
        send(null, message);
    }

    public BatchSummary getLastSummary() {
        return lastSummary;
    }
}
