package application.aicomic.enums;

public enum BookshelvesEnums {
    DELETED((byte) 0),
    ACTIVE((byte) 1),
    AVAILABLE((byte) 2);

    private final byte value;

    BookshelvesEnums(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static BookshelvesEnums fromValue(byte value) {
        for (BookshelvesEnums x : BookshelvesEnums.values()) {
            if (x.getValue() == value) {
                return x;
            }
        }
        throw new IllegalArgumentException("No BookshelvesEnums with value " + value);
    }
}
