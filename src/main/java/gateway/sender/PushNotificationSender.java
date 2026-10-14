package gateway.sender;

public class PushNotificationSender extends SimulatedSender {

    public static final int MAX_BODY_LENGTH = 120;

    public PushNotificationSender() {
        super("Push");
    }

    @Override
    protected String buildPayload(String recipient, String title, String body) {
        // a push banner is short, so long text gets cut
        String shown = body.length() > MAX_BODY_LENGTH
                ? body.substring(0, MAX_BODY_LENGTH - 3) + "..."
                : body;
        return "push to device " + recipient + ": [" + title + "] " + shown;
    }
}
