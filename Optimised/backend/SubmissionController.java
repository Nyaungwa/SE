/**
 * [DIAGRAM] SubmissionController — thin relay layer.
 *
 * Step 2: UI -> SubmissionController: submit(data)
 * Step 3: SubmissionController -> SubmissionService: processSubmission(data)
 *
 * [OPTIMISATION vs Baseline]
 * The baseline SubmissionController was a bloated God controller directly coupled
 * to Validator, Database, ReviewerManager, and EvaluationManager.
 *
 * The optimised SubmissionController has ONE responsibility: receive the request
 * from UI and delegate to SubmissionService. All business logic is gone from here.
 * This satisfies the Controller GRASP pattern properly.
 */
public class SubmissionController {

    private final SubmissionService submissionService;
    private Researcher researcher;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    public void setResearcher(Researcher researcher) {
        this.researcher = researcher;
    }

    // [DIAGRAM] Step 2: UI -> SubmissionController: submit(data)
    public String submit(String data) {
        TraceLogger.call("UI", "SubmissionController", "submit(data)");

        // [DIAGRAM] Step 3: SubmissionController -> SubmissionService: processSubmission(data)
        TraceLogger.call("SubmissionController", "SubmissionService", "processSubmission(data)");
        String result = submissionService.processSubmission(data, researcher);

        TraceLogger.returnVal("SubmissionService", "SubmissionController", result);
        if (!result.startsWith("ERROR")) {
            TraceLogger.returnVal("SubmissionController", "UI", result);
        } else {
            TraceLogger.returnVal("SubmissionController", "UI",
                                  "ERROR: Submission format invalid -- workflow aborted.");
        }
        return result;
    }
}
