/**
 * [DIAGRAM] Validator
 *
 * Step 4: SubmissionService -> Validator: validate(data)
 * Step 5: Validator -> SubmissionService: valid / invalid
 */
public class Validator {

    public static final class ValidationResult {
        public final boolean valid;
        public final String  reason;

        private ValidationResult(boolean valid, String reason) {
            this.valid  = valid;
            this.reason = reason;
        }

        public static ValidationResult ok()              { return new ValidationResult(true,  "VALID"); }
        public static ValidationResult fail(String why)  { return new ValidationResult(false, why); }
    }

    // [DIAGRAM] Step 4-5
    public ValidationResult validate(String data) {
        TraceLogger.call("SubmissionService", "Validator", "validate(data)");
        if (data == null || data.trim().isEmpty()) {
            ValidationResult r = ValidationResult.fail("INVALID -- data is null or empty");
            TraceLogger.returnVal("Validator", "SubmissionService", r.reason);
            return r;
        }
        if (data.trim().length() < 10) {
            ValidationResult r = ValidationResult.fail("INVALID -- data too short (min 10 chars)");
            TraceLogger.returnVal("Validator", "SubmissionService", r.reason);
            return r;
        }
        ValidationResult r = ValidationResult.ok();
        TraceLogger.returnVal("Validator", "SubmissionService", r.reason);
        return r;
    }
}
