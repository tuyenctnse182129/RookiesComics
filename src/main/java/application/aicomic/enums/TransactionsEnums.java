package application.aicomic.enums;

public enum TransactionsEnums {
    CANCELED((byte) 1),
    NOT_PAID((byte) 2),
    PROCESSING((byte) 3),
    PAID((byte) 4);

    private final byte value;

    TransactionsEnums(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
    public static TransactionsEnums fromValue(byte value) {
        for (TransactionsEnums x : TransactionsEnums.values()) {
            if (x.getValue() == value) {
                return x;
            }
        }
        throw new IllegalArgumentException("No TransactionsEnums with value " + value);
    }

    public enum Type {
        // Type Enums
        ROLE_UPGRADE((byte) 1),
        BUYING_STORIES((byte) 2),
        PAY_FEE((byte) 3);

        private final byte value;

        Type(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static Type fromValue(byte value) {
            for (Type x : Type.values()) {
                if (x.getValue() == value) {
                    return x;
                }
            }
            throw new IllegalArgumentException("No TransactionsEnums with value " + value);
        }
    }
}
