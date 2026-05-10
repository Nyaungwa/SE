package baseline;

import java.util.List;

/**
 * [DIAGRAM] ReviewerManager lifeline.
 *
 * Participates in the following interactions:
 *   1. SubmissionController -> ReviewerManager: getAvailableReviewers()
 *   2. ReviewerManager      -> Database:        fetchReviewers() -> reviewerList
 *   3. ReviewerManager      -> Reviewer:        filterConflicts(reviewerList)  [FLAW]
 *   4. ReviewerManager      -> Reviewer:        checkWorkload(reviewerList)    [FLAW]
 *   5. ReviewerManager      -> SubmissionController: filteredReviewers
 *
 * ------------------------------------------------------------------
 * BASELINE DESIGN FLAWS (intentionally preserved from diagram):
 *
 * A) ReviewerManager delegates filterConflicts() and checkWorkload()
 *    to a Reviewer object -- the Reviewer should not be responsible
 *    for filtering logic (Expert principle violation).
 *
 * B) ReviewerManager performs two separate round-trips: one to
 *    Database and two to Reviewer, when a single cohesive method
 *    inside ReviewerManager could handle all of this.
 *
 * Both flaws are preserved verbatim for Task 1.
 * ------------------------------------------------------------------
 */
public class ReviewerManager {

    private final Database database;

    /**
     * [DIAGRAM FLAW] A Reviewer instance used purely as a delegate for
     * filterConflicts() and checkWorkload() -- misplaced responsibility.
     */
    private final Reviewer reviewerDelegate;

    public ReviewerManager(Database database, Reviewer reviewerDelegate) {
        this.database         = database;
        this.reviewerDelegate = reviewerDelegate;
    }

    /**
     * [DIAGRAM] SubmissionController -> ReviewerManager: getAvailableReviewers()
     *
     * Retrieves a filtered list of reviewers eligible to assess the submission.
     * Internally performs three interactions exactly as specified in the diagram:
     *
     *   Step 1 -> Database: fetchReviewers()             -> reviewerList
     *   Step 2 -> Reviewer: filterConflicts(reviewerList)
     *   Step 3 -> Reviewer: checkWorkload(reviewerList)
     *   Returns filteredReviewers to SubmissionController
     *
     * @return filtered, available reviewer list
     */
    public List<Reviewer> getAvailableReviewers() {

        // [DIAGRAM] ReviewerManager -> Database: fetchReviewers() -> reviewerList
        TraceLogger.call("ReviewerManager", "Database", "fetchReviewers()");
        List<Reviewer> reviewerList = database.fetchReviewers();

        // [DIAGRAM] ReviewerManager -> Reviewer: filterConflicts(reviewerList)
        // [FLAW] This call goes to Reviewer instead of being handled internally
        TraceLogger.call("ReviewerManager", "Reviewer", "filterConflicts(reviewerList)");
        List<Reviewer> noConflicts = reviewerDelegate.filterConflicts(reviewerList);

        // [DIAGRAM] ReviewerManager -> Reviewer: checkWorkload(reviewerList)
        // [FLAW] Second separate call -- should be one combined filter operation
        TraceLogger.call("ReviewerManager", "Reviewer", "checkWorkload(reviewerList)");
        List<Reviewer> filteredReviewers = reviewerDelegate.checkWorkload(noConflicts);

        // [DIAGRAM] ReviewerManager -> SubmissionController: filteredReviewers
        TraceLogger.returnVal("ReviewerManager", "SubmissionController",
                              "filteredReviewers (" + filteredReviewers.size() + " eligible)");
        return filteredReviewers;
    }
}
