package application.aicomic.enums;

public enum ChaptersEnums {
    PENDING((byte) 0),
    LOCKED((byte) 1),
    UNLOCKED((byte) 2),
    DELETED((byte) 3);

    private final byte value;

    ChaptersEnums(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ChaptersEnums fromValue(byte value) {
        for (ChaptersEnums x : ChaptersEnums.values()) {
            if (x.getValue() == value) {
                return x;
            }
        }
        throw new IllegalArgumentException("No ChaptersEnums with value " + value);
    }
}
