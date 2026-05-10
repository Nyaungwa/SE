import java.util.Map;

/**
 * [DIAGRAM] DecisionEngine
 *
 * Step 16: EvaluationService -> DecisionEngine: determineOutcome(scores)
 * Step 17: DecisionEngine -> EvaluationService: outcome
 *
 * [OPTIMISATION vs Baseline]
 * The baseline exposed three self-calls (calculateAverage, checkConsensus, applyRules)
 * and used a three-branch alt block calling notifyAcceptance/notifyRejection/notifyRevision
 * separately. This violated High Cohesion and the Polymorphism GRASP pattern.
 *
 * The DecisionEngine is a Pure Fabrication (GRASP) that:
 *   1. Encapsulates all evaluation algorithm steps internally
 *   2. Returns a single outcome string replacing the three-branch alt block
 *   3. Maps directly to the consolidated DT2 decision table from Task 3
 *
 * Decision thresholds (from DT2):
 *   avg >= 75 AND consensus  -> accepted
 *   avg >= 50 AND consensus  -> revision
 *   otherwise                -> rejected
 */
public class DecisionEngine {

    private static final double ACCEPTANCE_THRESHOLD = 75.0;
    private static final double REVISION_THRESHOLD   = 50.0;
    private static final double CONSENSUS_SPREAD_MAX = 15.0;

    // [DIAGRAM] Step 16: EvaluationService -> DecisionEngine: determineOutcome(scores)
    public String determineOutcome(Map<String, Double> scores) {
        TraceLogger.call("EvaluationService", "DecisionEngine",
                         "determineOutcome(scores)");

        double  average   = calculateAverage(scores);
        boolean consensus = checkConsensus(scores);
        String  outcome   = applyRules(average, consensus);

        TraceLogger.returnVal("DecisionEngine", "EvaluationService",
                              "outcome = " + outcome.toUpperCase());
        return outcome;
    }

    // ── Internal steps (not exposed as separate diagram interactions) ────────

    private double calculateAverage(Map<String, Double> scores) {
        if (scores.isEmpty()) return 0.0;
        double sum = 0;
        for (double s : scores.values()) sum += s;
        double avg = Math.round((sum / scores.size()) * 100.0) / 100.0;
        TraceLogger.info("DecisionEngine", "average = " + TraceLogger.fmt(avg));
        return avg;
    }

    private boolean checkConsensus(Map<String, Double> scores) {
        if (scores.size() < 2) return true;
        double min = scores.values().stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = scores.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        boolean consensus = (max - min) <= CONSENSUS_SPREAD_MAX;
        TraceLogger.info("DecisionEngine",
                         "spread=" + TraceLogger.fmt(max - min) +
                         ", consensus=" + consensus);
        return consensus;
    }

    private String applyRules(double average, boolean consensus) {
        if (average >= ACCEPTANCE_THRESHOLD && consensus) return "accepted";
        if (average >= REVISION_THRESHOLD   && consensus) return "revision";
        return "rejected";
    }
}
