package application.aicomic.repositories;

import java.util.Optional;

import application.aicomic.enums.WalletType;
import application.aicomic.models.Wallets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import application.aicomic.models.Comics;

import application.aicomic.enums.WalletType;
import application.aicomic.models.Users;
import application.aicomic.models.Wallets;

@Repository
public interface WalletsRepository extends JpaRepository<Wallets, String> {

    // ✅ Add this method to find a wallet by user and wallet type
    Optional<Wallets> findByUserAndType(Users user, WalletType type);
    Optional<Wallets> findByUserIdAndType(String userId, WalletType type);
}
