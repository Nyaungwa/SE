import java.util.UUID;

/**
 * [DIAGRAM] Data object representing a research submission artefact.
 * Passed between SubmissionService, ReviewerSelectionService, and EvaluationService.
 */
public class Submission {

    private final String id;
    private final String data;
    private String status;

    public Submission(String data) {
        this.id     = "SUB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.data   = data;
        this.status = "PENDING";
    }

    public String getId()     { return id; }
    public String getData()   { return data; }
    public String getStatus() { return status; }
    public void   setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Submission{id='" + id + "', status='" + status + "'}";
    }
}
