package gateway.sender;

public class SmsMessageSender extends SimulatedSender {

    public static final int DEFAULT_MAX_LENGTH = 160;

    private final int maxLength;

    public SmsMessageSender() {
        this(DEFAULT_MAX_LENGTH);
    }

    public SmsMessageSender(int maxLength) {
        super("SMS");
        if (maxLength <= 0) {
            throw new IllegalArgumentException("SMS limit must be positive");
        }
        this.maxLength = maxLength;
    }

    @Override
    protected String buildPayload(String recipient, String title, String body) {
        // sms needs a phone number, optional + and 10-15 digits
        if (!recipient.matches("\\+?\\d{10,15}")) {
            return null;
        }
        String text = title.isBlank() ? body : title + ": " + body;
        if (text.length() > maxLength) {
            return null;
        }
        return "to " + recipient + ": " + text;
    }

    public int getMaxLength() {
        return maxLength;
    }
}
