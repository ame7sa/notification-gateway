package gateway;

import gateway.notification.BatchSummary;
import gateway.notification.ScheduledBatchNotification;
import gateway.sender.SmsMessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScheduledBatchNotificationTest {

    @Test
    @DisplayName("batch counts successful and failed deliveries")
    void summaryCountsFailures() {
        SmsMessageSender sms = new SmsMessageSender();
        ScheduledBatchNotification batch = new ScheduledBatchNotification(sms, List.of(
                "+77010000001", "+77010000002", "+77010000003", "+77010000004",
                "+77010000005", "+77010000006", "+77010000007", "+77010000008",
                "bad-number", "123"));

        batch.sendToAll("hello");

        BatchSummary summary = batch.getLastSummary();
        assertEquals(10, summary.total());
        assertEquals(8, summary.successful());
        assertEquals(2, summary.failed());
        assertEquals(8, sms.getSentMessages().size());
    }

    @Test
    @DisplayName("summary is printed in the format from the task")
    void summaryFormat() {
        BatchSummary summary = new BatchSummary(10, 8, 2);

        assertEquals("Total: 10\nSuccessful: 8\nFailed: 2", summary.toString());
    }

    @Test
    @DisplayName("everything fails if the provider is down")
    void allFailWhenUnavailable() {
        SmsMessageSender sms = new SmsMessageSender();
        sms.setAvailable(false);
        ScheduledBatchNotification batch =
                new ScheduledBatchNotification(sms, List.of("+77010000001", "+77010000002"));

        batch.sendToAll("hello");

        assertEquals(0, batch.getLastSummary().successful());
        assertEquals(2, batch.getLastSummary().failed());
    }

    @Test
    @DisplayName("empty recipient list is not allowed")
    void emptyListRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ScheduledBatchNotification(new SmsMessageSender(), List.of()));
    }
}
