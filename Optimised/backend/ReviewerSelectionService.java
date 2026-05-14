import java.util.ArrayList;
import java.util.List;

/**
 * Fetches and filters eligible reviewers in a single pass.
 * Replaces the two separate calls (filterConflicts + checkWorkload) that the baseline
 * incorrectly delegated to a Reviewer instance — Expert pattern violation fixed.
 */
public class ReviewerSelectionService {

    private final ReviewerRepository reviewerRepository;

    public ReviewerSelectionService(ReviewerRepository reviewerRepository) {
        this.reviewerRepository = reviewerRepository;
    }

    public List<Reviewer> getAvailableReviewers() {
        TraceLogger.call("SubmissionService", "ReviewerSelectionService",
                         "getAvailableReviewers()");

        List<Reviewer> allReviewers = reviewerRepository.findAvailableReviewers();
        List<Reviewer> eligible     = filterEligibleReviewers(allReviewers);

        TraceLogger.returnVal("ReviewerSelectionService", "SubmissionService",
                              "eligibleReviewers (" + eligible.size() + " eligible)");
        return eligible;
    }

    // Single combined filter — replaces the two separate Reviewer delegate calls from the baseline.
    private List<Reviewer> filterEligibleReviewers(List<Reviewer> reviewers) {
        TraceLogger.call("ReviewerSelectionService", "ReviewerSelectionService",
                         "filterEligibleReviewers(reviewerList)");

        List<Reviewer> eligible = new ArrayList<>();
        for (Reviewer r : reviewers) {
            if (r.hasConflict()) {
                TraceLogger.info("ReviewerSelectionService",
                                 "removing " + r.getName() + " (conflict of interest)");
            } else if (r.isOverloaded()) {
                TraceLogger.info("ReviewerSelectionService",
                                 "removing " + r.getName() +
                                 " (workload=" + r.getCurrentWorkload() + ", exceeds threshold)");
            } else {
                eligible.add(r);
            }
        }

        TraceLogger.returnVal("ReviewerSelectionService", "ReviewerSelectionService",
                              "filteredList (" + eligible.size() + " remaining)");
        return eligible;
    }
}
