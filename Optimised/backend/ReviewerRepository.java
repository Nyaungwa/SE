import java.util.ArrayList;
import java.util.List;

/** Reviewer data access. Decouples business logic from the data store via the Indirection pattern. */
public class ReviewerRepository {

    public List<Reviewer> findAvailableReviewers() {
        TraceLogger.call("ReviewerSelectionService", "ReviewerRepository",
                         "findAvailableReviewers()");

        List<Reviewer> reviewers = new ArrayList<>();
        // id, name, currentWorkload, hasConflict
        reviewers.add(new Reviewer("R001", "Dr. Smith",  2, false));
        reviewers.add(new Reviewer("R002", "Dr. Jones",  5, false));
        reviewers.add(new Reviewer("R003", "Dr. Patel",  1, false));
        reviewers.add(new Reviewer("R004", "Dr. Chen",   8, false)); // overloaded -> filtered
        reviewers.add(new Reviewer("R005", "Dr. Nguyen", 3, true));  // conflict -> filtered
        reviewers.add(new Reviewer("R006", "Dr. Okafor", 4, false));

        TraceLogger.db("returned " + reviewers.size() + " candidates");
        TraceLogger.returnVal("ReviewerRepository", "ReviewerSelectionService",
                              "reviewerList (" + reviewers.size() + " reviewers)");
        return reviewers;
    }
}
