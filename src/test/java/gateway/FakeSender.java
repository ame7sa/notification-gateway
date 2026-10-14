package gateway;

import gateway.sender.MessageSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiny sender that is NOT one of the project classes. Used in tests to show
 * that notifications only depend on the MessageSender interface.
 */
public class FakeSender implements MessageSender {

    public final List<String> bodies = new ArrayList<>();
    private final String name;
    private final boolean available;

    public FakeSender(String name, boolean available) {
        this.name = name;
        this.available = available;
    }

    @Override
    public boolean sendMessage(String recipient, String title, String body) {
        bodies.add(body);
        return true;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    @Override
    public String getChannelName() {
        return name;
    }
}
