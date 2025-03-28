package application.aicomic.dataAccess;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private String orderId;
    private byte newStatusByte;
}
