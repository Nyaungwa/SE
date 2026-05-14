

import java.util.ArrayList;
import java.util.List;

/**
 * Handles score collection, averaging, consensus check, and outcome routing.
 * Three self-calls (calculateAverage, checkConsensus, applyRules) and a three-branch
 * alt notification block are intentional baseline flaws preserved from the diagram.
 */
public class EvaluationManager {

    private static final double ACCEPTANCE_THRESHOLD  = 75.0;
    private static final double REJECTION_THRESHOLD   = 50.0;
    private static final double CONSENSUS_VARIANCE    = 15.0; // max allowed score spread

    private final Database            database;
    private final NotificationService notificationService;

    private final List<Double>  scores      = new ArrayList<>();
    private final List<String>  reviewerIds = new ArrayList<>();

    public EvaluationManager(Database database, NotificationService notificationService) {
        this.database            = database;
        this.notificationService = notificationService;
    }

    public void startEvaluation() {
        scores.clear();
        reviewerIds.clear();
    }

    public void submitScore(String reviewerId, double score) {
        TraceLogger.call("EvaluationManager", "Database",
                         "saveScore(score=" + TraceLogger.fmt(score)
                         + ", reviewer=" + reviewerId + ")");
        database.saveScore(reviewerId, score);

        scores.add(score);
        reviewerIds.add(reviewerId);
    }

    // Exposed as a diagram self-call instead of being a private detail — baseline flaw.
    private double calculateAverage() {
        if (scores.isEmpty()) {
            TraceLogger.info("EvaluationManager", "average = 0.00 (no scores)");
            return 0.0;
        }
        double sum = 0;
        for (double s : scores) sum += s;
        double avg = Math.round((sum / scores.size()) * 100.0) / 100.0;
        TraceLogger.info("EvaluationManager", "average = " + TraceLogger.fmt(avg));
        return avg;
    }

    // Exposed as a diagram self-call instead of being a private detail — baseline flaw.
    private boolean checkConsensus() {
        if (scores.size() < 2) {
            TraceLogger.info("EvaluationManager", "spread=0.00, consensus=true (single reviewer)");
            return true;
        }
        double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        boolean consensus = (max - min) <= CONSENSUS_VARIANCE;
        TraceLogger.info("EvaluationManager",
                         "spread=" + TraceLogger.fmt(max - min) + ", consensus=" + consensus);
        return consensus;
    }

    // Exposed as a diagram self-call instead of being a private detail — baseline flaw.
    private String applyRules(double average, boolean consensus) {
        String outcome;

        if (average >= ACCEPTANCE_THRESHOLD && consensus) {
            outcome = "accepted";
        } else if (average < REJECTION_THRESHOLD || !consensus) {
            outcome = "rejected";
        } else {
            outcome = "revision";
        }

        TraceLogger.info("EvaluationManager", "outcome = " + outcome.toUpperCase());
        return outcome;
    }

    // Called by SubmissionController after the reviewer score loop completes.
    public void finalizeEvaluation() {

        TraceLogger.info("EvaluationManager", "[self-call] calculateAverage()");
        double average = calculateAverage();

        TraceLogger.info("EvaluationManager", "[self-call] checkConsensus()");
        boolean consensus = checkConsensus();

        TraceLogger.info("EvaluationManager", "[self-call] applyRules()");
        String outcome = applyRules(average, consensus);

        System.out.println();

        // Three separate branches calling three separate notify methods — DRY violation, baseline flaw.
        if ("accepted".equals(outcome)) {
            TraceLogger.alt("[accepted]");
            TraceLogger.call("EvaluationManager", "NotificationService", "notifyAcceptance()");
            notificationService.notifyAcceptance();

        } else if ("rejected".equals(outcome)) {
            TraceLogger.alt("[rejected]");
            TraceLogger.call("EvaluationManager", "NotificationService", "notifyRejection()");
            notificationService.notifyRejection();

        } else {
            TraceLogger.alt("[revision]");
            TraceLogger.call("EvaluationManager", "NotificationService", "notifyRevision()");
            notificationService.notifyRevision();
        }
    }
}
