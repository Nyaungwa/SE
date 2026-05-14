

/** Thin passthrough between Researcher and SubmissionController. */
public class UI {

    private final SubmissionController submissionController;

    public UI(SubmissionController submissionController) {
        this.submissionController = submissionController;
    }

    public String submitResearchOutput(String data) {
        TraceLogger.call("UI", "SubmissionController", "submit(data)");
        return submissionController.submit(data);
    }
}
