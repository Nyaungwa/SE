import java.util.HashMap;
import java.util.Map;

/**
 * Persists reviewer scores. Accepts a single bulk save after all scores are collected,
 * replacing the per-reviewer saveScore() calls inside the loop from the baseline.
 */
public class EvaluationRepository {

    private final Map<String, Double> scoreStore = new HashMap<>();

    public void saveAllScores(Map<String, Double> scores) {
        TraceLogger.call("EvaluationService", "EvaluationRepository", "saveAllScores(scores)");
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
