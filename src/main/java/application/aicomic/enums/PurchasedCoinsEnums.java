package application.aicomic.enums;

public enum PurchasedCoinsEnums {

    // Coin Type Enums
    PURCHASED((byte) 1),
    BONUS((byte) 2);

    private final byte value;

    PurchasedCoinsEnums(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static PurchasedCoinsEnums fromValue(byte value) {
        for (PurchasedCoinsEnums x : PurchasedCoinsEnums.values()) {
            if (x.getValue() == value) {
                return x;
            }
        }
        throw new IllegalArgumentException("No PurchasedCoinsEnums with value " + value);
    }

    // For status
    public enum Status {
        PENDING((byte) 1),
        COMPLETED((byte) 2),
        CANCELLED((byte) 3);

        private final byte value;

        Status(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static Status fromValue(byte value) {
            for (Status x : Status.values()) {
                if (x.getValue() == value) {
                    return x;
                }
            }
            throw new IllegalArgumentException("No PurchasedCoinsEnums Status with value " + value);
        }
    }
}