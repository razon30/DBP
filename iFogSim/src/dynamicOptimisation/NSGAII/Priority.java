package dynamicOptimisation.NSGAII;

public enum Priority {

    HIGH(1),
    GENERAL(2);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

}
