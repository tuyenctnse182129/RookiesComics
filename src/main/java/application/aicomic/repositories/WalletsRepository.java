package application.aicomic.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.aicomic.enums.WalletType;
import application.aicomic.models.Users;
import application.aicomic.models.Wallets;

@Repository
public interface WalletsRepository extends JpaRepository<Wallets, String> {
    
    // ✅ Add this method to find a wallet by user and wallet type
    Optional<Wallets> findByUserAndType(Users user, WalletType type);
}
