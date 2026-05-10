import java.util.ArrayList;
import java.util.List;

/**
 * [DIAGRAM] ReviewerSelectionService
 *
 * Step 8:  SubmissionService -> ReviewerSelectionService: getAvailableReviewers()
 * Step 9:  ReviewerSelectionService -> ReviewerRepository: findAvailableReviewers()
 * Step 10: ReviewerRepository -> ReviewerSelectionService: reviewerList
 * Step 11: ReviewerSelectionService [self-call]: filterEligibleReviewers(reviewerList)
 * Step 12: ReviewerSelectionService -> SubmissionService: eligibleReviewers
 *
 * ── KEY OPTIMISATION ──────────────────────────────────────────────────────────
 * Baseline flaw (I-2, I-3 from Task 2):
 *   ReviewerManager called filterConflicts() and checkWorkload() as TWO SEPARATE
 *   method calls on a REVIEWER DELEGATE — wrong class, wrong responsibility.
 *
 * Optimised fix:
 *   ReviewerSelectionService owns a single internal filterEligibleReviewers()
 *   method that applies BOTH checks in ONE pass. No external delegate needed.
 *   This fixes the Expert Pattern violation and eliminates the redundant call.
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class ReviewerSelectionService {

    private final ReviewerRepository reviewerRepository;

    public ReviewerSelectionService(ReviewerRepository reviewerRepository) {
        this.reviewerRepository = reviewerRepository;
    }

    // [DIAGRAM] Step 8: SubmissionService -> ReviewerSelectionService: getAvailableReviewers()
    public List<Reviewer> getAvailableReviewers() {
        TraceLogger.call("SubmissionService", "ReviewerSelectionService",
                         "getAvailableReviewers()");

        // [DIAGRAM] Step 9-10: ReviewerSelectionService -> ReviewerRepository
        List<Reviewer> allReviewers = reviewerRepository.findAvailableReviewers();

        // [DIAGRAM] Step 11: ReviewerSelectionService [self-call]: filterEligibleReviewers()
        // [OPTIMISATION] Single combined filter replaces two separate calls on Reviewer delegate
        List<Reviewer> eligible = filterEligibleReviewers(allReviewers);

        // [DIAGRAM] Step 12: return eligible reviewers to SubmissionService
        TraceLogger.returnVal("ReviewerSelectionService", "SubmissionService",
                              "eligibleReviewers (" + eligible.size() + " eligible)");
        return eligible;
    }

    /**
     * [DIAGRAM] Step 11 — internal self-call.
     *
     * Applies conflict check AND workload check in a single pass.
     * Replaces the two separate filterConflicts() + checkWorkload() calls
     * from the baseline that were incorrectly placed on a Reviewer delegate.
     */
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
