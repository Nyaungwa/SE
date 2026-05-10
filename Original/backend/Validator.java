package baseline;

/**
 * [DIAGRAM] Validator lifeline.
 *
 * Receives: validateFormat(data) from SubmissionController
 * Returns:  valid / invalid signal back to SubmissionController
 *
 * NOTE (Baseline): Format validation is a separate round-trip between
 * SubmissionController and Validator, increasing interaction count.
 * This is intentionally preserved as specified in the diagram.
 */
public class Validator {

    /**
     * [DIAGRAM] SubmissionController -> Validator: validateFormat(data)
     *           Validator           -> SubmissionController: valid/invalid
     *
     * Checks that the submission data is non-null, non-empty, and
     * meets minimum length requirements.
     *
     * @param data the raw submission string
     * @return true if valid, false if invalid
     */
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
