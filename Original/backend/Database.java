package baseline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [DIAGRAM] Database lifeline.
 *
 * Participates in three separate interactions (an intentional baseline flaw):
 *   1. SubmissionController -> Database: saveSubmission(data) -> confirmation
 *   2. ReviewerManager      -> Database: fetchReviewers()     -> reviewerList
 *   3. EvaluationManager    -> Database: saveScore(score)
 *
 * NOTE (Baseline): The Database is called directly from three different
 * components with no repository abstraction, creating high coupling.
 * This is preserved exactly as specified in the diagram.
 */
public class Database {

    // --- In-memory stores (simulate persistence) ---
    private final Map<String, Submission> submissionStore = new HashMap<>();
    private final Map<String, Double>     scoreStore      = new HashMap<>();

    // ------------------------------------------------------------------
    // Interaction 1 -- saveSubmission
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] SubmissionController -> Database: saveSubmission(data)
     *           Database -> SubmissionController: confirmation
     *
     * Persists the submission and returns a confirmation token.
     *
     * @param submission the submission to persist
     * @return confirmation string
     */
    public String saveSubmission(Submission submission) {
        submissionStore.put(submission.getId(), submission);
        String confirmation = "CONF-" + submission.getId();
        TraceLogger.db("stored submission " + submission.getId());
        TraceLogger.returnVal("Database", "SubmissionController", "confirmation = " + confirmation);
        return confirmation;
    }

    // ------------------------------------------------------------------
    // Interaction 2 -- fetchReviewers
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] ReviewerManager -> Database: fetchReviewers()
     *           Database        -> ReviewerManager: reviewerList
     *
     * Returns a fixed pool of candidate reviewers.
     * In a real system this would query a persistent store.
     *
     * @return full list of candidate Reviewer objects
     */
    public List<Reviewer> fetchReviewers() {
        List<Reviewer> reviewerList = new ArrayList<>();
        // id, name, currentWorkload, hasConflict
        reviewerList.add(new Reviewer("R001", "Dr. Smith",   2, false));
        reviewerList.add(new Reviewer("R002", "Dr. Jones",   5, false));
        reviewerList.add(new Reviewer("R003", "Dr. Patel",   1, false));
        reviewerList.add(new Reviewer("R004", "Dr. Chen",    8, false)); // high workload -> filtered
        reviewerList.add(new Reviewer("R005", "Dr. Nguyen",  3, true));  // conflict -> filtered
        reviewerList.add(new Reviewer("R006", "Dr. Okafor",  4, false));

        TraceLogger.db("returned " + reviewerList.size() + " candidates");
        TraceLogger.returnVal("Database", "ReviewerManager",
                              "reviewerList (" + reviewerList.size() + " reviewers)");
        return reviewerList;
    }

    // ------------------------------------------------------------------
    // Interaction 3 -- saveScore
    // ------------------------------------------------------------------

    /**
     * [DIAGRAM] EvaluationManager -> Database: saveScore(score)
     *
     * Persists an individual reviewer score for a submission.
     *
     * @param reviewerId the reviewer who submitted the score
     * @param score      the numeric score
     */
    public void saveScore(String reviewerId, double score) {
        scoreStore.put(reviewerId, score);
        TraceLogger.db("score=" + TraceLogger.fmt(score) + " saved for reviewer " + reviewerId);
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    public Map<String, Double> getAllScores() {
        return new HashMap<>(scoreStore);
    }
}
