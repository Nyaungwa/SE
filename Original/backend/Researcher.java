package baseline;

/**
 * [DIAGRAM] Researcher -- the initiating actor.
 *
 * Participates in two interactions:
 *   1. Researcher -> UI: submitResearchOutput(data)
 *   2. NotificationService -> Researcher: sendNotification()
 *
 * As an actor (not a fully-fledged system component), Researcher
 * initiates the workflow and receives the final outcome notification.
 */
public class Researcher {

    private final String name;
    private UI ui;    // set after construction to avoid circular dependency

    public Researcher(String name) {
        this.name = name;
    }

    public void setUI(UI ui) {
        this.ui = ui;
    }

    // ------------------------------------------------------------------
    // [DIAGRAM] Researcher -> UI: submitResearchOutput(data)
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] Researcher -> UI: submitResearchOutput(data)
     *
     * Entry point -- the Researcher initiates the submission workflow
     * by handing data to the UI layer.
     *
     * @param data the raw research output to submit
     */
    public void submitResearchOutput(String data) {
        TraceLogger.call("Researcher:" + name, "UI", "submitResearchOutput(data)");
        ui.submitResearchOutput(data);
    }

    // ------------------------------------------------------------------
    // [DIAGRAM] NotificationService -> Researcher: sendNotification()
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] NotificationService -> Researcher: sendNotification()
     *
     * Receives the final outcome notification at the end of the workflow.
     *
     * @param message the notification content
     */
    public void receiveNotification(String message) {
        TraceLogger.separator();
        TraceLogger.returnVal("NotificationService", "Researcher:" + name, message);
        TraceLogger.separator();
    }

    public String getName() { return name; }
}
