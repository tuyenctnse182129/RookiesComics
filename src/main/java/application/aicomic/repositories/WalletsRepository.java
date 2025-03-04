package application.aicomic.repositories;

import application.aicomic.enums.WalletType;
import application.aicomic.models.Wallets;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

import application.aicomic.models.Comics;

public interface WalletsRepository extends JpaRepository<Wallets, String> {
    Optional<Wallets> findByUserIdAndType(String userId, WalletType type);
}
