/**
 * Single unified notification entry point. Replaces the three separate outcome methods
 * (notifyAcceptance, notifyRejection, notifyRevision) called from a scattered alt block in the baseline.
 */
public class NotificationService {

    public void notifyResearcher(Researcher researcher, String outcome) {
        TraceLogger.call("EvaluationService", "NotificationService",
                         "notifyResearcher(researcher, outcome=" + outcome + ")");

        String message = composeMessage(outcome);

        TraceLogger.call("NotificationService", "Researcher:" + researcher.getName(),
                         "sendNotification()");
        researcher.receiveNotification(message);
    }

    // Message content maps to DT2 action entries.
    private String composeMessage(String outcome) {
        switch (outcome) {
            case "accepted":
                return "CONGRATULATIONS: Your research submission has been ACCEPTED.";
            case "revision":
                return "ACTION REQUIRED: Your research submission requires REVISION. "
                     + "Please address reviewer comments and resubmit.";
            default:
                return "NOTICE: Your research submission has been REJECTED. "
                     + "Please review the reviewer feedback.";
        }
    }
}
