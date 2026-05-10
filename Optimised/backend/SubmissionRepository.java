import java.util.HashMap;
import java.util.Map;

/**
 * [DIAGRAM] SubmissionRepository
 *
 * Step 7 (valid flow): SubmissionService -> SubmissionRepository: saveSubmission(data)
 *
 * [OPTIMISATION vs Baseline]
 * The baseline called Database.saveSubmission() directly from SubmissionController,
 * coupling the controller to persistence. The optimised design routes all submission
 * persistence through this repository, decoupling business logic from the data store.
 */
public class SubmissionRepository {

    private final Map<String, Submission> store = new HashMap<>();

    // [DIAGRAM] Step 7
    public String saveSubmission(Submission submission) {
        TraceLogger.call("SubmissionService", "SubmissionRepository", "saveSubmission(data)");
        store.put(submission.getId(), submission);
        String confirmation = "CONF-" + submission.getId();
        TraceLogger.db("stored submission " + submission.getId());
        TraceLogger.returnVal("SubmissionRepository", "SubmissionService",
                              "confirmation = " + confirmation);
        return confirmation;
    }

    public Submission findById(String id) {
        return store.get(id);
    }
}
