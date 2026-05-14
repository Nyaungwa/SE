

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory persistence layer called directly by three separate components.
 * No repository abstraction — high coupling is an intentional baseline flaw.
 */
public class Database {

    private final Map<String, Submission> submissionStore = new HashMap<>();
    private final Map<String, Double>     scoreStore      = new HashMap<>();

    public String saveSubmission(Submission submission) {
        submissionStore.put(submission.getId(), submission);
        String confirmation = "CONF-" + submission.getId();
        TraceLogger.db("stored submission " + submission.getId());
        TraceLogger.returnVal("Database", "SubmissionController", "confirmation = " + confirmation);
        return confirmation;
    }

    public List<Reviewer> fetchReviewers() {
        List<Reviewer> reviewerList = new ArrayList<>();
        // id, name, currentWorkload, hasConflict
        reviewerList.add(new Reviewer("R001", "Dr. Smith",   2, false));
        reviewerList.add(new Reviewer("R002", "Dr. Jones",   5, false));
        reviewerList.add(new Reviewer("R003", "Dr. Patel",   1, false));
        reviewerList.add(new Reviewer("R004", "Dr. Chen",    8, false)); // overloaded -> filtered
        reviewerList.add(new Reviewer("R005", "Dr. Nguyen",  3, true));  // conflict -> filtered
        reviewerList.add(new Reviewer("R006", "Dr. Okafor",  4, false));

        TraceLogger.db("returned " + reviewerList.size() + " candidates");
        TraceLogger.returnVal("Database", "ReviewerManager",
                              "reviewerList (" + reviewerList.size() + " reviewers)");
        return reviewerList;
    }

    public void saveScore(String reviewerId, double score) {
        scoreStore.put(reviewerId, score);
        TraceLogger.db("score=" + TraceLogger.fmt(score) + " saved for reviewer " + reviewerId);
    }

    public Map<String, Double> getAllScores() {
        return new HashMap<>(scoreStore);
    }
}
