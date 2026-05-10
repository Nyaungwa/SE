import java.util.List;

/**
 * [DIAGRAM] SubmissionService
 *
 * Step 3:  SubmissionController -> SubmissionService: processSubmission(data)
 * Step 4:  SubmissionService -> Validator: validate(data)
 * Step 7:  SubmissionService -> SubmissionRepository: saveSubmission(data)
 * Step 8:  SubmissionService -> ReviewerSelectionService: getAvailableReviewers()
 * Step 13: SubmissionService -> Reviewer [loop]: assignReviewers()
 * Step 14: SubmissionService -> EvaluationService: startEvaluation()
 *
 * [OPTIMISATION vs Baseline]
 * The baseline SubmissionController was a bloated controller coupled to 4 classes.
 * SubmissionService takes over business logic, leaving SubmissionController as a
 * thin relay. This satisfies High Cohesion and the Controller GRASP pattern.
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

    // [DIAGRAM] Step 3: SubmissionController -> SubmissionService: processSubmission(data)
    public String processSubmission(String data, Researcher researcher) {

        // [DIAGRAM] Step 4-5: validate
        Validator.ValidationResult result = validator.validate(data);

        // [DIAGRAM] alt [invalid]
        if (!result.valid) {
            TraceLogger.alt("[invalid] - returning validation error");
            TraceLogger.returnVal("SubmissionService", "SubmissionController",
                                  "validationError: " + result.reason);
            return "ERROR: " + result.reason;
        }

        // [DIAGRAM] alt [valid]
        TraceLogger.alt("[valid] - continuing workflow");

        // [DIAGRAM] Step 7: save submission
        Submission submission = new Submission(data);
        submissionRepository.saveSubmission(submission);

        // [DIAGRAM] Step 8-12: get eligible reviewers
        List<Reviewer> eligibleReviewers = reviewerSelectionService.getAvailableReviewers();

        // [DIAGRAM] Step 13: loop — assign reviewers
        TraceLogger.loop("assign reviewers [for each eligible reviewer]");
        for (Reviewer reviewer : eligibleReviewers) {
            TraceLogger.call("SubmissionService", "Reviewer:" + reviewer.getId(),
                             "assignReviewers()");
            reviewer.assignReview(submission);
        }

        // [DIAGRAM] Step 14: start evaluation
        evaluationService.startEvaluation();

        // [DIAGRAM] Steps 15-19: collect scores, save, decide, notify
        evaluationService.finalizeEvaluation(researcher, eligibleReviewers);

        String successMsg = "SUCCESS: Submission " + submission.getId() + " processed.";
        TraceLogger.result(successMsg);
        return successMsg;
    }
}
