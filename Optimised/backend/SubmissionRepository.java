import java.util.HashMap;
import java.util.Map;

/** Submission persistence. Decouples business logic from the data store. */
public class SubmissionRepository {

    private final Map<String, Submission> store = new HashMap<>();

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
