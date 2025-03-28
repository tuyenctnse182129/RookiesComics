package application.aicomic.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import application.aicomic.models.Orders;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, String> {
    Optional<Orders> findByUserIdAndStatus(String userId, byte status);
}
