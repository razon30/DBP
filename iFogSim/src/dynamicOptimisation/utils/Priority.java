package dynamicOptimisation.utils;

public enum Priority {
    HIGH(1),
    GENERAL(2);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public static Priority fromValue(int value) {
        for (Priority priority : Priority.values()) {
            if (priority.getPriority() == value) {
                return priority;
            }
        }
        throw new IllegalArgumentException("No enum constant with value: " + value);
    }

    public int getPriority() {
        return priority;
    }
}

