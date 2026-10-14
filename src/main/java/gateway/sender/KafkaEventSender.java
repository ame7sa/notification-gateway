package gateway.sender;

public class KafkaEventSender extends SimulatedSender {

    private final String topic;
    private long nextOffset = 0;

    public KafkaEventSender() {
        this("notifications");
    }

    public KafkaEventSender(String topic) {
        super("Kafka");
        this.topic = topic;
    }

    @Override
    protected String buildPayload(String recipient, String title, String body) {
        String value = "{\"title\":\"" + escape(title) + "\",\"body\":\"" + escape(body) + "\"}";
        return "topic=" + topic + " key=" + recipient + " offset=" + (nextOffset++) + " value=" + value;
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
