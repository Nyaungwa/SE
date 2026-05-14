

/**
 * Initiating actor. UI is injected post-construction to break the circular Researcher<->UI dependency.
 */
public class Researcher {

    private final String name;
    private UI ui;

    public Researcher(String name) {
        this.name = name;
    }

    public void setUI(UI ui) {
        this.ui = ui;
    }

    public void submitResearchOutput(String data) {
        TraceLogger.call("Researcher:" + name, "UI", "submitResearchOutput(data)");
        ui.submitResearchOutput(data);
    }

    public void receiveNotification(String message) {
        TraceLogger.separator();
        TraceLogger.returnVal("NotificationService", "Researcher:" + name, message);
        TraceLogger.separator();
    }

    public String getName() { return name; }
}
