package application.aicomic.dataAccess;

import application.aicomic.enums.TransactionsEnums;
import lombok.Data;

@Data
public class TransactionRequest {
    private double amount;
    private TransactionsEnums.Type type;
    private TransactionsEnums status;
    private String walletId;
}
