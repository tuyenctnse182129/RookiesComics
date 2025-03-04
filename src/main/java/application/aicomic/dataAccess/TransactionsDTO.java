package application.aicomic.dataAccess;

import application.aicomic.enums.PurchasedCoinsEnums;
import application.aicomic.enums.TransactionsEnums;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionsDTO {
    private String transactionId;
    private LocalDateTime transactionTime;
    private double amount;
    private byte status;
    private TransactionsEnums.Type type;
    private String orderId;
    private String walletId;
    private String purchasedCoinId;
}