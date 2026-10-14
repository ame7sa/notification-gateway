package gateway.notification;

public record BatchSummary(int total, int successful, int failed) {

    @Override
    public String toString() {
        return "Total: " + total + "\n"
                + "Successful: " + successful + "\n"
                + "Failed: " + failed;
    }
}
