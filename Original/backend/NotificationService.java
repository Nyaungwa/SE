
/**
 * Dispatches outcome notifications to the researcher.
 * Three separate methods called from a scattered alt block — DRY violation.
 * A single notify(outcome) would be cleaner; preserved from the baseline diagram.
 */
public class NotificationService {

    private final Researcher researcher;

    public NotificationService(Researcher researcher) {
        this.researcher = researcher;
    }

    public void notifyAcceptance() {
        String message = "CONGRATULATIONS: Your research submission has been ACCEPTED.";
        sendNotification(message);
    }

    public void notifyRejection() {
        String message = "NOTICE: Your research submission has been REJECTED. "
                       + "Please review the reviewer feedback.";
        sendNotification(message);
    }

    public void notifyRevision() {
        String message = "ACTION REQUIRED: Your research submission requires REVISION. "
                       + "Please address reviewer comments and resubmit.";
        sendNotification(message);
    }

    private void sendNotification(String message) {
        TraceLogger.call("NotificationService", "Researcher:" + researcher.getName(),
                         "sendNotification()");
        researcher.receiveNotification(message);
    }
}
