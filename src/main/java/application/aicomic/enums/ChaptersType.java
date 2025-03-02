package application.aicomic.enums;

public enum ChaptersType {
    FREE((byte) 0),
    PAID((byte) 1);

    private final byte value;

    private ChaptersType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ChaptersType fromValue(byte value) {
        for (ChaptersType type : ChaptersType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("No ChaptersType with value " + value);
    }
}
