

import java.util.List;
import java.util.UUID;

/**
 * Central workflow orchestrator. Directly coupled to four components (Validator, Database,
 * ReviewerManager, EvaluationManager) — God Controller anti-pattern. Baseline flaw preserved.
 */
public class SubmissionController {

    private final Validator         validator;
    private final Database          database;
    private final ReviewerManager   reviewerManager;
    private final EvaluationManager evaluationManager;

    public SubmissionController(Validator         validator,
                                Database          database,
                                ReviewerManager   reviewerManager,
                                EvaluationManager evaluationManager) {
        this.validator         = validator;
        this.database          = database;
        this.reviewerManager   = reviewerManager;
        this.evaluationManager = evaluationManager;
    }

    public String submit(String data) {

        TraceLogger.call("SubmissionController", "Validator", "validateFormat(data)");
        boolean isValid = validator.validateFormat(data);
        System.out.println();

        if (!isValid) {
            TraceLogger.alt("[invalid] - return error");
            String errorMsg = "ERROR: Submission format invalid -- workflow aborted.";
            TraceLogger.returnVal("SubmissionController", "UI", errorMsg);
            return errorMsg;
        }

        TraceLogger.alt("[valid] - continuing workflow");

        TraceLogger.call("SubmissionController", "Database", "saveSubmission(data)");
        Submission submission = new Submission(generateId(), data);
        String confirmation = database.saveSubmission(submission);
        System.out.println();

        TraceLogger.call("SubmissionController", "ReviewerManager", "getAvailableReviewers()");
        List<Reviewer> filteredReviewers = reviewerManager.getAvailableReviewers();
        System.out.println();

        TraceLogger.loop("assign reviewers [for each eligible reviewer]");
        for (Reviewer reviewer : filteredReviewers) {
            TraceLogger.call("SubmissionController", "Reviewer:" + reviewer.getId(), "assignReview()");
            reviewer.assignReview(submission);
        }
        System.out.println();

        TraceLogger.call("SubmissionController", "EvaluationManager", "startEvaluation()");
        evaluationManager.startEvaluation();
        System.out.println();

        TraceLogger.loop("each reviewer submits score [for each reviewer]");
        for (Reviewer reviewer : filteredReviewers) {
            reviewer.submitScoreTo(evaluationManager);
        }
        System.out.println();

        TraceLogger.call("SubmissionController", "EvaluationManager", "finalizeEvaluation()");
        evaluationManager.finalizeEvaluation();
        System.out.println();

        String result = "SUCCESS: Submission " + submission.getId() + " processed.";
        TraceLogger.result(result);
        return result;
    }

    private String generateId() {
        return "SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
