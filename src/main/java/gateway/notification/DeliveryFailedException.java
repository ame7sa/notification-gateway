package gateway.notification;

public class DeliveryFailedException extends RuntimeException {

    public DeliveryFailedException(String message) {
        super(message);
    }
}
