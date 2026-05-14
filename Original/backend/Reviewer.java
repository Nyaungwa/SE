

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reviewer domain object. Also carries filterConflicts() and checkWorkload() methods
 * that ReviewerManager calls on a delegate instance — wrong class, wrong responsibility.
 * Expert pattern violation preserved from the baseline diagram.
 */
public class Reviewer {

    private static final int WORKLOAD_THRESHOLD = 6;

    private final String  id;
    private final String  name;
    private final int     currentWorkload;
    private final boolean hasConflict;

    private Submission assignedSubmission;

    public Reviewer(String id, String name, int currentWorkload, boolean hasConflict) {
        this.id              = id;
        this.name            = name;
        this.currentWorkload = currentWorkload;
        this.hasConflict     = hasConflict;
    }

    // Called by ReviewerManager on a delegate — filtering logic that belongs in ReviewerManager.
    public List<Reviewer> filterConflicts(List<Reviewer> reviewerList) {
        List<Reviewer> noConflicts = new ArrayList<>();
        for (Reviewer r : reviewerList) {
            if (!r.hasConflict) {
                noConflicts.add(r);
            } else {
                TraceLogger.info("Reviewer", "removing " + r.name + " (conflict of interest)");
            }
        }
        TraceLogger.returnVal("Reviewer", "ReviewerManager",
                              "filteredList (" + noConflicts.size() + " remaining)");
        return noConflicts;
    }

    // Called by ReviewerManager on a delegate — filtering logic that belongs in ReviewerManager.
    public List<Reviewer> checkWorkload(List<Reviewer> reviewerList) {
        List<Reviewer> available = new ArrayList<>();
        for (Reviewer r : reviewerList) {
            if (r.currentWorkload < WORKLOAD_THRESHOLD) {
                available.add(r);
            } else {
                TraceLogger.info("Reviewer", "removing " + r.name
                                 + " (workload=" + r.currentWorkload + ", exceeds threshold)");
            }
        }
        TraceLogger.returnVal("Reviewer", "ReviewerManager",
                              "filteredList (" + available.size() + " remaining)");
        return available;
    }

    public void assignReview(Submission submission) {
        this.assignedSubmission = submission;
        TraceLogger.info("Reviewer:" + id, "assigned to " + submission.getId());
    }

    public void submitScoreTo(EvaluationManager evaluationManager) {
        double score = generateScore();
        TraceLogger.call("Reviewer:" + id, "EvaluationManager",
                         "submitScore(score=" + TraceLogger.fmt(score) + ")");
        evaluationManager.submitScore(id, score);
    }

    // Score is seeded from the reviewer id so runs are deterministic.
    private double generateScore() {
        Random rng = new Random(id.hashCode());
        return Math.round((50 + rng.nextDouble() * 50) * 10.0) / 10.0;
    }

    public String  getId()              { return id; }
    public String  getName()            { return name; }
    public int     getCurrentWorkload() { return currentWorkload; }
    public boolean hasConflict()        { return hasConflict; }

    @Override
    public String toString() {
        return "Reviewer{id='" + id + "', name='" + name + "'}";
    }
}
