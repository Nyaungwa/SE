

import java.util.List;

/**
 * Retrieves and filters available reviewers.
 * Filtering is delegated to a Reviewer instance via two separate calls — misplaced
 * responsibility (Expert pattern violation). Baseline flaw preserved intentionally.
 */
public class ReviewerManager {

    private final Database database;

    // Used purely as a delegate for filterConflicts() and checkWorkload() — wrong design.
    private final Reviewer reviewerDelegate;

    public ReviewerManager(Database database, Reviewer reviewerDelegate) {
        this.database         = database;
        this.reviewerDelegate = reviewerDelegate;
    }

    public List<Reviewer> getAvailableReviewers() {

        TraceLogger.call("ReviewerManager", "Database", "fetchReviewers()");
        List<Reviewer> reviewerList = database.fetchReviewers();

        // Two separate calls to a Reviewer delegate instead of one internal filter — baseline flaw.
        TraceLogger.call("ReviewerManager", "Reviewer", "filterConflicts(reviewerList)");
        List<Reviewer> noConflicts = reviewerDelegate.filterConflicts(reviewerList);

        TraceLogger.call("ReviewerManager", "Reviewer", "checkWorkload(reviewerList)");
        List<Reviewer> filteredReviewers = reviewerDelegate.checkWorkload(noConflicts);

        TraceLogger.returnVal("ReviewerManager", "SubmissionController",
                              "filteredReviewers (" + filteredReviewers.size() + " eligible)");
        return filteredReviewers;
    }
}
