package gateway.sender;

public class EmailMessageSender extends SimulatedSender {

    private static final String SIGNATURE = "--\nNotification Gateway Team";

    public EmailMessageSender() {
        super("Email");
    }

    @Override
    protected String buildPayload(String recipient, String title, String body) {
        if (!recipient.contains("@")) {
            return null;
        }
        String subject = title.isBlank() ? "(no subject)" : title;
        return "To: " + recipient + "\n"
                + "Subject: " + subject + "\n\n"
                + body + "\n\n"
                + SIGNATURE;
    }
}
