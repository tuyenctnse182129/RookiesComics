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
}
