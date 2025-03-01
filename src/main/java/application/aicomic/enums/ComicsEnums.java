package application.aicomic.enums;

public enum ComicsEnums {
    DELETED((byte) 0),
    PROCESSING((byte) 1),
    ONGOING((byte) 2),
    COMPLETED((byte) 3),
    STOPPED((byte) 4),
    APPROVED((byte) 5),
    REJECTED((byte) 6);

    private final byte value;

    ComicsEnums(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ComicsEnums fromValue(byte value) {
        for (ComicsEnums x : ComicsEnums.values()) {
            if (x.getValue() == value) {
                return x;
            }
        }
        throw new IllegalArgumentException("No ComicsEnums with value" + value);
    }
}
