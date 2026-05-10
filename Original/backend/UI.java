package baseline;

/**
 * [DIAGRAM] UI lifeline.
 *
 * Acts as the entry point between the Researcher actor and the
 * SubmissionController. Participates in two interactions:
 *
 *   Receives:  Researcher -> UI: submitResearchOutput(data)
 *   Delegates: UI -> SubmissionController: submit(data)
 *   Returns:   result back toward Researcher (via return value)
 */
public class UI {

    private final SubmissionController submissionController;

    public UI(SubmissionController submissionController) {
        this.submissionController = submissionController;
    }

    /**
     * [DIAGRAM] Researcher -> UI: submitResearchOutput(data)
     *           UI -> SubmissionController: submit(data)
     *
     * Receives the researcher's submission request and forwards the data
     * to the SubmissionController to begin the workflow.
     *
     * @param data the raw research output data
     * @return the result from the SubmissionController
     */
    public String submitResearchOutput(String data) {
        // [DIAGRAM] UI -> SubmissionController: submit(data)
        TraceLogger.call("UI", "SubmissionController", "submit(data)");
        return submissionController.submit(data);
    }
}
