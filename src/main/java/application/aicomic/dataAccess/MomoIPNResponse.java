package application.aicomic.dataAccess;

import lombok.Data;

@Data
public class MomoIPNResponse {
    private String partnerCode;
    private String orderId;
    private String userId;
    private String requestId;
    private long amount;
    private String orderInfo;
    private int resultCode;
    private String message;
    private String responseTime;
    private String extraData;
    private String signature;
}
