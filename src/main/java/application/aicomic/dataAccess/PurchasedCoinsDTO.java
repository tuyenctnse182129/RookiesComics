package application.aicomic.dataAccess;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import application.aicomic.enums.PurchasedCoinsEnums;
import application.aicomic.models.Transactions;
import application.aicomic.models.Users;
import lombok.Data;

@Data
public class PurchasedCoinsDTO {
    private String purchasedCoinId;
    private BigDecimal amount;
    private BigDecimal numberOfCoin;
    private PurchasedCoinsEnums type;
    private PurchasedCoinsEnums.Status status;
    private String description;
    private LocalDateTime purchaseTime;
    private String transactionCode;
    private String userId;
    private Users user;
    private List<Transactions> transactions;
}
