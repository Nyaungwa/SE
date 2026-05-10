package baseline;

/**
 * [DIAGRAM] NotificationService lifeline.
 *
 * Participates in the alt decision block interactions:
 *   [accepted]  -> EvaluationManager -> NotificationService: notifyAcceptance()
 *   [rejected]  -> EvaluationManager -> NotificationService: notifyRejection()
 *   [revision]  -> EvaluationManager -> NotificationService: notifyRevision()
 *
 * And the final outbound interaction:
 *   NotificationService -> Researcher: sendNotification()
 *
 * ------------------------------------------------------------------
 * BASELINE DESIGN FLAW (intentionally preserved from diagram):
 * Three separate notification methods (notifyAcceptance, notifyRejection,
 * notifyRevision) are called from within a scattered alt block. This
 * duplicates nearly identical behaviour and violates DRY. A unified
 * notify(outcome) method with a decision table would be cleaner.
 * This flaw is preserved verbatim for Task 1.
 * ------------------------------------------------------------------
 */
public class NotificationService {

    private final Researcher researcher;

    public NotificationService(Researcher researcher) {
        this.researcher = researcher;
    }

    // ------------------------------------------------------------------
    // [DIAGRAM] alt [accepted] -- EvaluationManager -> NotificationService
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] alt [accepted]: EvaluationManager -> NotificationService: notifyAcceptance()
     *
     * Composes an acceptance message and dispatches it to the Researcher.
     */
    public void notifyAcceptance() {
        String message = "CONGRATULATIONS: Your research submission has been ACCEPTED.";
        sendNotification(message);
    }

    // ------------------------------------------------------------------
    // [DIAGRAM] alt [rejected] -- EvaluationManager -> NotificationService
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] alt [rejected]: EvaluationManager -> NotificationService: notifyRejection()
     *
     * Composes a rejection message and dispatches it to the Researcher.
     */
    public void notifyRejection() {
        String message = "NOTICE: Your research submission has been REJECTED. "
                       + "Please review the reviewer feedback.";
        sendNotification(message);
    }

    // ------------------------------------------------------------------
    // [DIAGRAM] alt [revision] -- EvaluationManager -> NotificationService
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] alt [revision]: EvaluationManager -> NotificationService: notifyRevision()
     *
     * Composes a revision-required message and dispatches it to the Researcher.
     */
    public void notifyRevision() {
        String message = "ACTION REQUIRED: Your research submission requires REVISION. "
                       + "Please address reviewer comments and resubmit.";
        sendNotification(message);
    }

    // ------------------------------------------------------------------
    // [DIAGRAM] NotificationService -> Researcher: sendNotification()
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] NotificationService -> Researcher: sendNotification()
     *
     * Final outbound interaction -- delivers the composed message to the Researcher.
     *
     * @param message the notification content
     */
    private void sendNotification(String message) {
        TraceLogger.call("NotificationService", "Researcher:" + researcher.getName(),
                         "sendNotification()");
        researcher.receiveNotification(message);
    }
}
