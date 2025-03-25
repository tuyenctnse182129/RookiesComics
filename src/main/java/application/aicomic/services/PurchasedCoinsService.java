package application.aicomic.services;

import application.aicomic.dataAccess.MonthlyRevenueDTO;
import application.aicomic.dataAccess.PurchasedCoinsDTO;
import application.aicomic.enums.OrdersEnums;
import application.aicomic.enums.PurchasedCoinsEnums;
import application.aicomic.enums.WalletType;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.Orders;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.PurchasedCoinsRepository;
import application.aicomic.repositories.WalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchasedCoinsService {
    @Autowired
    private final PurchasedCoinsRepository purchasedCoinsRepository;
    @Autowired
    private WalletsService walletsService;
    @Autowired
    private final Mapper mapper;

    public PurchasedCoinsService(PurchasedCoinsRepository purchasedCoinsRepository, Mapper mapper, WalletsService walletsService) {
        this.purchasedCoinsRepository = purchasedCoinsRepository;
        this.mapper = mapper;
        this.walletsService = walletsService;
    }

    // Get all PurchasedCoins
    public List<PurchasedCoins> getAllPurchasedCoins() {
        return purchasedCoinsRepository.findAll();
    }

    // Get PurchasedCoins by ID
    public PurchasedCoins getPurchasedCoinsById(String id) {
        return purchasedCoinsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchasedCoins not found with id: " + id));
    }

    // Add new PurchasedCoins
    public PurchasedCoins addPurchasedCoins(PurchasedCoins purchasedCoins) {
        return purchasedCoinsRepository.save(purchasedCoins);
    }

    // Update PurchasedCoins
    public PurchasedCoins updatePurchasedCoins(String id, PurchasedCoinsDTO purchasedCoinsDTO) {
        PurchasedCoins purchasedCoins = purchasedCoinsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PurchasedCoins not found"));

        // Use the mapper to update the entity from the DTO
        mapper.updatePurchasedCoins(purchasedCoins, purchasedCoinsDTO);
        return purchasedCoinsRepository.save(purchasedCoins);
    }

    // Delete PurchasedCoins (or set status to CANCELED)
    public PurchasedCoins deletePurchasedCoins(String id) {
        Optional<PurchasedCoins> purchasedCoins = purchasedCoinsRepository.findById(id);
        if (purchasedCoins.isPresent()) {
            PurchasedCoins coins = purchasedCoins.get();
            coins.setStatus(PurchasedCoinsEnums.CANCELED.getValue()); // Set status as available (soft delete)
            return purchasedCoinsRepository.save(coins);
        }
        return null;
    }

    public PurchasedCoins findByOrderId(String orderId) {
        return purchasedCoinsRepository.findByTransactionCode(orderId)
                .orElseThrow(() -> new RuntimeException("PurchasedCoins not found with orderId: " + orderId));
    }

    public PurchasedCoins updateStatus(String orderId, PurchasedCoinsEnums status) {
        PurchasedCoins purchasedCoins = purchasedCoinsRepository.findByTransactionCode(orderId)
                .orElseThrow(() -> new RuntimeException("PurchasedCoins not found with orderId: " + orderId));

        purchasedCoins.setStatus(status.getValue());
        return purchasedCoinsRepository.save(purchasedCoins);
    }

    public PurchasedCoins createPurchasedCoins(String transactionCode, String content, String bankName,
                                               double amount, double numberOfCoin, PurchasedCoinsEnums status,
                                               String userId) {
        PurchasedCoins purchasedCoins = new PurchasedCoins();
        purchasedCoins.setTransactionCode(transactionCode);
        purchasedCoins.setContent(content);
        purchasedCoins.setBankName(bankName);
        purchasedCoins.setAmount(amount);
        purchasedCoins.setNumberOfCoin(numberOfCoin);
        purchasedCoins.setStatus(status.getValue());
        purchasedCoins.setPurchaseTime(LocalDateTime.now());
        purchasedCoins.setUserId(userId);

        return purchasedCoinsRepository.save(purchasedCoins);
    }

    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        List<PurchasedCoins> paidPurchasedCoins = purchasedCoinsRepository.findByStatus(PurchasedCoinsEnums.PAID.getValue());

        // Lấy múi giờ hệ thống
        ZoneId zoneId = ZoneId.systemDefault();

        // Nhóm theo tháng và tính tổng doanh thu mua xu
        Map<String, Double> revenueMap = paidPurchasedCoins.stream()
                .collect(Collectors.groupingBy(
                        purchasedCoins -> {
                            LocalDateTime purchaseTime = purchasedCoins.getPurchaseTime();
                            ZonedDateTime zonedDateTime = purchaseTime.atZone(zoneId);
                            return zonedDateTime.getYear() + "-" + zonedDateTime.getMonthValue();
                        },
                        Collectors.summingDouble(PurchasedCoins::getAmount)
                ));

        // Chuyển Map thành danh sách DTO
        return revenueMap.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    return new MonthlyRevenueDTO(month, year, entry.getValue());
                })
                .sorted(Comparator.comparing(MonthlyRevenueDTO::getYear)
                        .thenComparing(MonthlyRevenueDTO::getMonth))
                .collect(Collectors.toList());
    }
}
