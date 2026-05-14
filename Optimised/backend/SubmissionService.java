import java.util.List;

/**
 * Business logic for submission processing — validation, persistence, reviewer selection,
 * and evaluation hand-off. Replaces the bloated SubmissionController from the baseline.
 */
public class SubmissionService {

    private final Validator                validator;
    private final SubmissionRepository     submissionRepository;
    private final ReviewerSelectionService reviewerSelectionService;
    private final EvaluationService        evaluationService;

    public SubmissionService(Validator                validator,
                             SubmissionRepository     submissionRepository,
                             ReviewerSelectionService reviewerSelectionService,
                             EvaluationService        evaluationService) {
        this.validator                = validator;
        this.submissionRepository     = submissionRepository;
        this.reviewerSelectionService = reviewerSelectionService;
        this.evaluationService        = evaluationService;
    }

    public String processSubmission(String data, Researcher researcher) {

        Validator.ValidationResult result = validator.validate(data);

        if (!result.valid) {
            TraceLogger.alt("[invalid] - returning validation error");
            TraceLogger.returnVal("SubmissionService", "SubmissionController",
                                  "validationError: " + result.reason);
            return "ERROR: " + result.reason;
        }

        TraceLogger.alt("[valid] - continuing workflow");

        Submission submission = new Submission(data);
        submissionRepository.saveSubmission(submission);

        List<Reviewer> eligibleReviewers = reviewerSelectionService.getAvailableReviewers();

        TraceLogger.loop("assign reviewers [for each eligible reviewer]");
        for (Reviewer reviewer : eligibleReviewers) {
            TraceLogger.call("SubmissionService", "Reviewer:" + reviewer.getId(),
                             "assignReviewers()");
            reviewer.assignReview(submission);
        }

        evaluationService.startEvaluation();
        evaluationService.finalizeEvaluation(researcher, eligibleReviewers);

        String successMsg = "SUCCESS: Submission " + submission.getId() + " processed.";
        TraceLogger.result(successMsg);
        return successMsg;
    }
}
