package application.aicomic.dataAccess;

import lombok.Data;

@Data
public class UpdateOrderStatusRequest {
    private String userId;
    private byte newStatusByte;
}
