package application.aicomic.repositories;

import java.util.List;

import application.aicomic.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import application.aicomic.models.PurchasedCoins;

@Repository
public interface PurchasedCoinsRepository extends JpaRepository<PurchasedCoins, String> {

    List<PurchasedCoins> findByUserId(String userId);

    List<PurchasedCoins> findByStatus(byte status);
}
