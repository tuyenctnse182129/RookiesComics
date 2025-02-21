package application.aicomic.services;

import application.aicomic.dataAccess.PurchasedCoinsDTO;
import application.aicomic.enums.PurchasedCoinsEnums;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.repositories.PurchasedCoinsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchasedCoinsService {
    
    private final PurchasedCoinsRepository purchasedCoinsRepository;
    private final Mapper mapper;

    public PurchasedCoinsService(PurchasedCoinsRepository purchasedCoinsRepository, Mapper mapper) {
        this.purchasedCoinsRepository = purchasedCoinsRepository;
        this.mapper = mapper;
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

    // Delete PurchasedCoins (or set status to CANCELLED)
    public PurchasedCoins deletePurchasedCoins(String id) {
        Optional<PurchasedCoins> purchasedCoins = purchasedCoinsRepository.findById(id);
        if (purchasedCoins.isPresent()) {
            PurchasedCoins coins = purchasedCoins.get();
            coins.setStatus(PurchasedCoinsEnums.Status.CANCELLED.getValue()); // Set status as available (soft delete)
            return purchasedCoinsRepository.save(coins);
        }
        return null;
    }
}
