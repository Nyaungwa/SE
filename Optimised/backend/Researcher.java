/**
 * [DIAGRAM] Researcher — initiating actor.
 *
 * Step 1:  Researcher -> UI: submitResearchOutput(data)
 * Step 17: NotificationService -> Researcher: sendNotification(message)
 */
public class Researcher {

    private final String name;
    private UI ui;

    public Researcher(String name) { this.name = name; }

    public void setUI(UI ui) { this.ui = ui; }

    // [DIAGRAM] Step 1: Researcher -> UI: submitResearchOutput(data)
    public void submitResearchOutput(String data) {
        System.out.println();
        TraceLogger.call("Researcher:" + name, "UI", "submitResearchOutput(data)");
        ui.submit(data);
    }

    // [DIAGRAM] Step 17: NotificationService -> Researcher: sendNotification(message)
    public void receiveNotification(String message) {
        TraceLogger.separator();
        TraceLogger.returnVal("NotificationService", "Researcher:" + name, message);
        TraceLogger.separator();
    }

    public String getName() { return name; }
}
