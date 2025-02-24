package application.aicomic.enums;

public enum OrderDetailsEnums {
    INACTIVE((byte) 0),
    ACTIVE((byte) 1)
    ;
    private final byte value;

    OrderDetailsEnums(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static OrderDetailsEnums fromValue(byte value) {
        for (OrderDetailsEnums orderDetailEnums : OrderDetailsEnums.values()) {
            if (orderDetailEnums.getValue() == value) {
                return orderDetailEnums;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}
