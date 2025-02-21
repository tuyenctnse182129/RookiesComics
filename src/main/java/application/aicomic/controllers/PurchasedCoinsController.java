package application.aicomic.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import application.aicomic.dataAccess.PurchasedCoinsDTO;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.services.PurchasedCoinsService;

@RequestMapping("/purchasedCoins")
@RestController
public class PurchasedCoinsController {
    
    private final PurchasedCoinsService purchasedCoinsService;

    public PurchasedCoinsController(PurchasedCoinsService purchasedCoinsService) {
        this.purchasedCoinsService = purchasedCoinsService;
    }

    @GetMapping
    public List<PurchasedCoins> getAllPurchasedCoins() {
        return purchasedCoinsService.getAllPurchasedCoins();
    }

    @PostMapping
    public PurchasedCoins addPurchasedCoins(@RequestBody PurchasedCoins purchasedCoins) {
        return purchasedCoinsService.addPurchasedCoins(purchasedCoins);
    }

    @PutMapping("/{updateId}")
    public PurchasedCoins updatePurchasedCoins(@PathVariable String updateId, @RequestBody PurchasedCoinsDTO purchasedCoinsDTO) {
        return purchasedCoinsService.updatePurchasedCoins(updateId, purchasedCoinsDTO);
    }

    @GetMapping("/{id}")
    public PurchasedCoins getPurchasedCoinsById(@PathVariable String id) {
        return purchasedCoinsService.getPurchasedCoinsById(id);
    }

    @DeleteMapping("/{deleteId}")
    public PurchasedCoins deletePurchasedCoins(@PathVariable String deleteId) {
        return purchasedCoinsService.deletePurchasedCoins(deleteId);
    }
}

