package application.aicomic.services;

import application.aicomic.dataAccess.PurchasedCoinsDTO;
import application.aicomic.enums.PurchasedCoinsEnums;
import application.aicomic.enums.WalletType;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.PurchasedCoinsRepository;
import application.aicomic.repositories.WalletsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    public void savePurchasedCoinToDB(String userId,String numberOfCoin, String transactionCode, String amount, String bankName, String payDate, boolean isSuccess) {
        try {
            System.out.println("🔹 Đang lưu giao dịch...");
            System.out.println("Mã giao dịch: " + transactionCode);
            System.out.println("Số tiền: " + amount);
            System.out.println("Ngân hàng: " + bankName);
            System.out.println("Ngày thanh toán: " + payDate);
            System.out.println("Trạng thái: " + (isSuccess ? "PAID" : "NOT_PAID"));


            PurchasedCoins purchasedCoins = new PurchasedCoins();
            System.out.println("userId: " + purchasedCoins.getUserId());
            purchasedCoins.setUserId(userId); // Gán userId từ request
            purchasedCoins.setNumberOfCoin(Double.parseDouble(numberOfCoin));
            purchasedCoins.setTransactionCode(transactionCode);
            purchasedCoins.setAmount(Double.parseDouble(amount)/100);
            purchasedCoins.setBankName(bankName);
            purchasedCoins.setPurchaseTime(LocalDateTime.parse(payDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            purchasedCoins.setStatus(isSuccess ? PurchasedCoinsEnums.PAID.getValue() : PurchasedCoinsEnums.NOT_PAID.getValue());

            purchasedCoinsRepository.save(purchasedCoins);

            if (isSuccess) {
                double coin = Double.parseDouble(numberOfCoin);
                boolean updated = walletsService.updateBalance(userId, coin);
                if (!updated) {
                    System.out.println("Cập nhật trạng thái đơn hàng thất bại");
                    return;
                }
            }

            System.out.println("✅ Giao dịch đã lưu thành công!");
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi lưu giao dịch:");
            e.printStackTrace();
        }
    }

}
