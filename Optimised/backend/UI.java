/**
 * [DIAGRAM] UI — entry point between Researcher actor and SubmissionController.
 *
 * Receives: Researcher -> UI: submitResearchOutput(data)
 * Sends:    UI -> SubmissionController: submit(data)
 */
public class UI {

    private final SubmissionController submissionController;

    public UI(SubmissionController submissionController) {
        this.submissionController = submissionController;
    }

    public String submit(String data) {
        return submissionController.submit(data);
    }
}
