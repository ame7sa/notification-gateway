package gateway.notification;

import gateway.sender.MessageSender;

public class MarketingNotification extends Notification {

    private static final String TITLE = "Special offer";
    private static final String FOOTER = "Reply STOP to unsubscribe.";

    private final String campaign;

    public MarketingNotification(MessageSender sender) {
        this(sender, "PROMO");
    }

    public MarketingNotification(MessageSender sender, String campaign) {
        super(sender);
        this.campaign = campaign;
    }

    @Override
    public void send(String recipient, String message) {
        String body = "[" + campaign + "] " + message + "\n\n" + FOOTER;
        deliver(recipient, TITLE, body);
    }
}
