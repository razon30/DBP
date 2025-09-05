package dynamicOptimisation.utils;

public enum ResponsivenessConstraints {
    START_ACTIVITY("Start Activity"),
    END_ACTIVITY("End Activity"),
    RUNNING_ACTIVITY("Running Activity");

    private final String label;

    ResponsivenessConstraints(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
