import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [DIAGRAM] EvaluationService
 *
 * Step 14: SubmissionService -> EvaluationService: startEvaluation()
 * Step 15: loop — Reviewer -> EvaluationService: submitScores(scores)
 * Step 16: EvaluationService -> EvaluationRepository: saveAllScores(scores)  [after loop]
 * Step 17: EvaluationService -> DecisionEngine: determineOutcome(scores)
 * Step 18: EvaluationService -> NotificationService: notifyResearcher(researcher, outcome)
 *
 * [OPTIMISATION vs Baseline]
 * The baseline had EvaluationManager expose three self-calls (calculateAverage,
 * checkConsensus, applyRules) and called saveScore() inside the reviewer loop —
 * one database write per reviewer. EvaluationService fixes both:
 *   1. Internal evaluation algorithm delegated to DecisionEngine (hidden)
 *   2. Scores accumulated in memory; saveAllScores() called ONCE after the loop
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

    // [DIAGRAM] Step 14: SubmissionService -> EvaluationService: startEvaluation()
    public void startEvaluation() {
        TraceLogger.call("SubmissionService", "EvaluationService", "startEvaluation()");
        collectedScores.clear();
    }

    // [DIAGRAM] Step 15 loop: Reviewer -> EvaluationService: submitScores(scores)
    // Scores are ACCUMULATED here — NOT saved to DB inside the loop
    public void receiveScore(String reviewerId, double score) {
        collectedScores.put(reviewerId, score);
    }

    /**
     * Called by SubmissionService after the reviewer score loop completes.
     * Executes steps 16-18 in sequence.
     */
    public void finalizeEvaluation(Researcher researcher, List<Reviewer> reviewers) {

        // Trigger each reviewer to submit their score
        TraceLogger.loop("each reviewer submits score [for each reviewer]");
        for (Reviewer reviewer : reviewers) {
            // [DIAGRAM] Step 15: Reviewer -> EvaluationService: submitScores(scores)
            reviewer.submitScoreTo(this);
        }

        // [DIAGRAM] Step 16: EvaluationService -> EvaluationRepository: saveAllScores(scores)
        // [OPTIMISATION] Single bulk save after loop — replaces N individual saveScore() calls
        evaluationRepository.saveAllScores(new HashMap<>(collectedScores));

        // [DIAGRAM] Step 17: EvaluationService -> DecisionEngine: determineOutcome(scores)
        String outcome = decisionEngine.determineOutcome(new HashMap<>(collectedScores));

        // [DIAGRAM] Step 18: EvaluationService -> NotificationService: notifyResearcher()
        // [OPTIMISATION] Single unified call replaces three-branch alt block
        TraceLogger.alt("[" + outcome + "]");
        notificationService.notifyResearcher(researcher, outcome);
    }
}
