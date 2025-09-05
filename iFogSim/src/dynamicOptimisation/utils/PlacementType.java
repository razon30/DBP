package dynamicOptimisation.utils;

public enum PlacementType {
    CLOUD_ONLY(1),
    CLOUD_FOG_NON_OPTIMISE(2),
    CLOUD_FOG_OPTIMISE(3),
    DISTRIBUTED_MICROSERVICES_PLACEMENT(4);

    private final int value;

    PlacementType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
