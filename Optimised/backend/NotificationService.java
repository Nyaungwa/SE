/**
 * [DIAGRAM] NotificationService
 *
 * Step 18: EvaluationService -> NotificationService: notifyResearcher(researcher, outcome)
 * Step 19: NotificationService -> Researcher: sendNotification(message)
 *
 * [OPTIMISATION vs Baseline]
 * The baseline had three separate methods: notifyAcceptance(), notifyRejection(),
 * notifyRevision() — called from three separate alt branches.
 *
 * The optimised design uses a single notifyResearcher(researcher, outcome) method.
 * The outcome parameter drives the message content, replacing the scattered
 * branching logic and satisfying the Polymorphism GRASP pattern.
 */
public class NotificationService {

    // [DIAGRAM] Step 18: EvaluationService -> NotificationService: notifyResearcher(researcher, outcome)
    public void notifyResearcher(Researcher researcher, String outcome) {
        TraceLogger.call("EvaluationService", "NotificationService",
                         "notifyResearcher(researcher, outcome=" + outcome + ")");

        String message = composeMessage(outcome);

        // [DIAGRAM] Step 19: NotificationService -> Researcher: sendNotification(message)
        TraceLogger.call("NotificationService", "Researcher:" + researcher.getName(),
                         "sendNotification()");
        researcher.receiveNotification(message);
    }

    /**
     * Composes the notification message based on outcome.
     * Replaces the three separate notify methods from the baseline.
     * Maps directly to DT2 action entries from Task 3.
     */
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
