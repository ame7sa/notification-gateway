package gateway;

import gateway.notification.BatchSummary;
import gateway.notification.EncryptedNotification;
import gateway.notification.Notification;
import gateway.notification.ScheduledBatchNotification;
import gateway.notification.UrgentNotification;
import gateway.sender.EmailMessageSender;
import gateway.sender.SmsMessageSender;
import gateway.sender.TelegramMessageSender;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        urgentOverTelegram();
        encryptedOverEmail();
        batchOverSms();
        runtimeSwitching();
        fallbackToSms();
    }

    private static void header(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }

    // 1
    private static void urgentOverTelegram() {
        header("1. UrgentNotification + Telegram");
        Notification notification = new UrgentNotification(new TelegramMessageSender());
        notification.send("@aibar", "Server is down");
    }

    // 2
    private static void encryptedOverEmail() {
        header("2. EncryptedNotification + Email");
        EncryptedNotification notification = new EncryptedNotification(new EmailMessageSender());
        notification.send("aibar@example.com", "Your grade is 95");
        System.out.println("  (original text was: Your grade is 95)");
    }

    // 3
    private static void batchOverSms() {
        header("3. ScheduledBatchNotification + SMS");
        List<String> recipients = List.of(
                "+77011111101", "+77011111102", "+77011111103", "+77011111104",
                "+77011111105", "+77011111106", "+77011111107", "+77011111108",
                "not-a-number", "12345"   // these two can't be delivered by SMS
        );
        ScheduledBatchNotification batch = new ScheduledBatchNotification(new SmsMessageSender(), recipients);
        batch.sendToAll("Lecture moved to room 305");

        BatchSummary summary = batch.getLastSummary();
        System.out.println();
        System.out.println(summary);
    }

    // 4
    private static void runtimeSwitching() {
        header("4. Runtime switching: Telegram -> Email");
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        String user = "aibar@example.com";

        Notification notification = new UrgentNotification(telegram);
        notification.send(user, "Deploy finished");

        System.out.println("  ... switching sender, same notification object ...");
        notification.setSender(email);
        notification.send(user, "Deploy finished");
    }

    // 5
    private static void fallbackToSms() {
        header("5. Telegram unavailable -> fallback to SMS");
        TelegramMessageSender telegram = new TelegramMessageSender();
        SmsMessageSender sms = new SmsMessageSender();
        telegram.setAvailable(false);

        UrgentNotification notification = new UrgentNotification(telegram, sms);
        notification.send("+77011234567", "Database is not responding");
        System.out.println("  delivered via: " + notification.getLastUsedChannel());
    }
}
