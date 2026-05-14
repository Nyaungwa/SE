

/** Submission data object. Passed between components throughout the workflow. */
public class Submission {

    private final String id;
    private final String data;
    private String status;

    public Submission(String id, String data) {
        this.id     = id;
        this.data   = data;
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
