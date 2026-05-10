import java.util.HashMap;
import java.util.Map;

/**
 * [DIAGRAM] EvaluationRepository
 *
 * Step 15: EvaluationService -> EvaluationRepository: saveAllScores(scores)
 *
 * [OPTIMISATION vs Baseline]
 * The baseline called Database.saveScore() inside the reviewer loop — one database
 * call per reviewer score. The optimised design accumulates all scores in memory
 * and persists them in a single bulk saveAllScores() call after the loop closes.
 * This reduces database interactions from N calls to 1.
 */
public class EvaluationRepository {

    private final Map<String, Double> scoreStore = new HashMap<>();

    // [DIAGRAM] Step 15 — bulk save after all scores collected
    public void saveAllScores(Map<String, Double> scores) {
        TraceLogger.call("EvaluationService", "EvaluationRepository",
                         "saveAllScores(scores)");
        scoreStore.putAll(scores);
        TraceLogger.db("saved " + scores.size() + " scores in one batch");
        for (Map.Entry<String, Double> entry : scores.entrySet()) {
            TraceLogger.db("score=" + TraceLogger.fmt(entry.getValue()) +
                           " saved for reviewer " + entry.getKey());
        }
    }

    public Map<String, Double> getAllScores() {
        return new HashMap<>(scoreStore);
    }
}
