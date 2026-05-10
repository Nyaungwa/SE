package baseline;

/**
 * Data object representing a research submission artefact.
 * Passed between components throughout the submission workflow.
 *
 * [DIAGRAM] The "data" payload carried by submitResearchOutput(data),
 * submit(data), and saveSubmission(data) interactions.
 */
public class Submission {

    private final String id;
    private final String data;
    private String status;

    public Submission(String id, String data) {
        this.id   = id;
        this.data = data;
        this.status = "PENDING";
    }

    public String getId()     { return id; }
    public String getData()   { return data; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Submission{id='" + id + "', status='" + status + "'}";
    }
}
