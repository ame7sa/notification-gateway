package gateway.sender;

/**
 * Implementor in the Bridge pattern. Notifications only know about this
 * interface, they never see the concrete providers.
 */
public interface MessageSender {

    boolean sendMessage(
            String recipient,
            String title,
            String body
    );

    boolean isAvailable();

    String getChannelName();
}
