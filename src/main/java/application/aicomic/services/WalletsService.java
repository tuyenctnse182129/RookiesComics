package application.aicomic.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import application.aicomic.enums.WalletType;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.models.Wallets;
import application.aicomic.repositories.WalletsRepository;
import application.aicomic.mapper.Mapper;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
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

    public Wallets getMainWalletByUserId(String userId) {
        return walletsRepository.findByUserIdAndType(userId, WalletType.MAIN)
                .orElseThrow(() -> new RuntimeException("Main wallet not found for user: " + userId));
    }

    public Wallets getPromotionWalletByUserId(String userId) {
        return walletsRepository.findByUserIdAndType(userId, WalletType.PROMOTION)
                .orElseThrow(() -> new RuntimeException("Promotion wallet not found for user: " + userId));
    }

    @Transactional
    public boolean updateBalance(String userId, double coin) {
        try {
            Wallets wallets = walletsRepository.findByUserIdAndType(userId, WalletType.MAIN)
                    .orElseGet(() -> {
                        Wallets newWallet = new Wallets();
                        newWallet.setUserId(userId);
                        newWallet.setType(WalletType.MAIN);
                        newWallet.setBalance(0.0);
                        newWallet.setUpdatedDate(LocalDateTime.now());
                        return walletsRepository.save(newWallet);
                    });

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