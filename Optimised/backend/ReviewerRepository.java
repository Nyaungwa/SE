import java.util.ArrayList;
import java.util.List;

/**
 * [DIAGRAM] ReviewerRepository
 *
 * Step 9: ReviewerSelectionService -> ReviewerRepository: findAvailableReviewers()
 * Step 10: ReviewerRepository -> ReviewerSelectionService: reviewerList
 *
 * [OPTIMISATION vs Baseline]
 * The baseline called Database.fetchReviewers() directly from ReviewerManager,
 * coupling business logic to the database. The ReviewerRepository isolates the
 * data access concern, satisfying the Indirection GRASP pattern.
 */
public class ReviewerRepository {

    // [DIAGRAM] Step 9-10
    public List<Reviewer> findAvailableReviewers() {
        TraceLogger.call("ReviewerSelectionService", "ReviewerRepository",
                         "findAvailableReviewers()");

        List<Reviewer> reviewers = new ArrayList<>();
        reviewers.add(new Reviewer("R001", "Dr. Smith",  2, false));
        reviewers.add(new Reviewer("R002", "Dr. Jones",  5, false));
        reviewers.add(new Reviewer("R003", "Dr. Patel",  1, false));
        reviewers.add(new Reviewer("R004", "Dr. Chen",   8, false)); // overloaded
        reviewers.add(new Reviewer("R005", "Dr. Nguyen", 3, true));  // conflict
        reviewers.add(new Reviewer("R006", "Dr. Okafor", 4, false));

        TraceLogger.db("returned " + reviewers.size() + " candidates");
        TraceLogger.returnVal("ReviewerRepository", "ReviewerSelectionService",
                              "reviewerList (" + reviewers.size() + " reviewers)");
        return reviewers;
    }
}
