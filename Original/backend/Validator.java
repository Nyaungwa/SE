

/**
 * Validates submission format. A separate round-trip per submission — interaction overhead
 * that the optimised design keeps but routes through a dedicated service.
 */
public class Validator {

    public boolean validateFormat(String data) {

        if (data == null || data.trim().isEmpty()) {
            TraceLogger.returnVal("Validator", "SubmissionController",
                                  "INVALID -- data is null or empty");
            return false;
        }
        if (data.trim().length() < 10) {
            TraceLogger.returnVal("Validator", "SubmissionController",
                                  "INVALID -- data too short (min 10 chars)");
            return false;
        }

        TraceLogger.returnVal("Validator", "SubmissionController", "VALID");
        return true;
    }
}
