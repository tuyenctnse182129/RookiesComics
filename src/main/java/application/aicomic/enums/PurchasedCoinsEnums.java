package application.aicomic.enums;

public enum PurchasedCoinsEnums {

    CANCELED((byte) 1),
    NOT_PAID((byte) 2),
    PROCESSING((byte) 3),
    PAID((byte) 4);

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
        throw new IllegalArgumentException("No PurchasedCoinsEnums Status with value " + value);
    }

}