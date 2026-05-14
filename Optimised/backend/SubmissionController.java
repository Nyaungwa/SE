/**
 * Thin relay — receives the UI request and hands it to SubmissionService.
 * All business logic lives in SubmissionService; this class has exactly one dependency.
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

    public String submit(String data) {
        TraceLogger.call("UI", "SubmissionController", "submit(data)");
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
