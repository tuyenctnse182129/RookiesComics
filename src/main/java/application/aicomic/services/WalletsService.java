package application.aicomic.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import application.aicomic.enums.WalletType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.enums.OrdersEnums;
import application.aicomic.models.Orders;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.WalletsRepository;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.Users;
import java.time.LocalDateTime;

@Service
public class WalletsService {
    private final WalletsRepository walletsRepository;
    private Mapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ComicsService.class);

    public WalletsService(WalletsRepository walletsRepository) {
        this.walletsRepository = walletsRepository;
    }

    // read all wallets
    public List<Wallets> getAllWallets() {
        return walletsRepository.findAll();
    }

    // read wallet by ID
    public Wallets getWalletById(String walletsId) {
        return walletsRepository.findById(walletsId).orElse(null);
    }

    // create new wallet
    public Wallets createWallets(Wallets wallets) {
        wallets.setBalance(0);
        return walletsRepository.save(wallets);
    }

    public Wallets saveWallets(Wallets wallets) {
        return walletsRepository.save(wallets);
    }

    public Wallets updateWallets(String id, WalletsDTO walletsDTO) {
        Wallets wallets = walletsRepository.findById(id).orElseThrow(() -> new RuntimeException("Orders not found"));
        mapper.updateWallets(wallets, walletsDTO);
        return walletsRepository.save(wallets);
    }

    public boolean updateBalance(String userId, double coin) {
        try {
            Wallets wallets = walletsRepository.findByUserIdAndType(userId, WalletType.MAIN)
                    .orElseThrow(() -> new RuntimeException("Wallets not found"));
            wallets.setBalance(wallets.getBalance() + coin);
            wallets.setUpdatedDate(LocalDateTime.now());
            walletsRepository.save(wallets);
            return true;
        } catch (Exception e) {
            logger.error("❌ Lỗi khi cập nhật số dư ví: ", e);
            return false;
        }
    }


}
