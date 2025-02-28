package application.aicomic.enums;

public enum OrdersEnums {
    UNORDERED((byte) 0),
    PENDING((byte) 1),
    COMPLETED((byte) 2),
    CANCELLED((byte) 3);

    private final byte order_status;

    OrdersEnums(byte order_status) {
        this.order_status = order_status;
    }

    public byte getOrder_status() {
        return order_status;
    }
    public static OrdersEnums fromOrderStatus(byte order_status) {
        for (OrdersEnums order : OrdersEnums.values()) {
            if (order.getOrder_status() == order_status) {
                return order;
            }
        }
        throw new IllegalArgumentException("Invalid order status: " + order_status);
    }
}
