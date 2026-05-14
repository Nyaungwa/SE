import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinates score collection, bulk persistence, outcome determination, and notification.
 * Scores accumulate in memory during the reviewer loop; saveAllScores() fires once after —
 * replacing the N individual database writes from the baseline.
 */
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final DecisionEngine       decisionEngine;
    private final NotificationService  notificationService;

    private final Map<String, Double> collectedScores = new HashMap<>();

    public EvaluationService(EvaluationRepository evaluationRepository,
                             DecisionEngine       decisionEngine,
                             NotificationService  notificationService) {
        this.evaluationRepository = evaluationRepository;
        this.decisionEngine       = decisionEngine;
        this.notificationService  = notificationService;
    }

    public void startEvaluation() {
        TraceLogger.call("SubmissionService", "EvaluationService", "startEvaluation()");
        collectedScores.clear();
    }

    // Scores are accumulated here, not written to DB yet.
    public void receiveScore(String reviewerId, double score) {
        collectedScores.put(reviewerId, score);
    }

    // Called by SubmissionService after the reviewer loop; executes bulk save, decision, and notification.
    public void finalizeEvaluation(Researcher researcher, List<Reviewer> reviewers) {

        TraceLogger.loop("each reviewer submits score [for each reviewer]");
        for (Reviewer reviewer : reviewers) {
            reviewer.submitScoreTo(this);
        }

        evaluationRepository.saveAllScores(new HashMap<>(collectedScores));

        String outcome = decisionEngine.determineOutcome(new HashMap<>(collectedScores));

        TraceLogger.alt("[" + outcome + "]");
        notificationService.notifyResearcher(researcher, outcome);
    }
}
