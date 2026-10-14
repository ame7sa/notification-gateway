package gateway.sender;

public class TelegramMessageSender extends SimulatedSender {

    public TelegramMessageSender() {
        super("Telegram");
    }

    @Override
    protected String buildPayload(String recipient, String title, String body) {
        // telegram understands markdown, so the title goes in bold
        String text = title.isBlank() ? body : "*" + title + "*\n" + body;
        return "chat " + recipient + ":\n" + text;
    }
}
