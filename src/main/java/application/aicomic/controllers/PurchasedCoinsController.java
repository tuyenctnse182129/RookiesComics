package application.aicomic.controllers;

import java.util.List;

import application.aicomic.repositories.PurchasedCoinsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import application.aicomic.dataAccess.PurchasedCoinsDTO;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.services.PurchasedCoinsService;

@RequestMapping("/purchasedCoins")
@RestController
public class PurchasedCoinsController {
    @Autowired
    private final PurchasedCoinsService purchasedCoinsService;
    @Autowired
    private PurchasedCoinsRepository purchasedCoinsRepository;

    public PurchasedCoinsController(PurchasedCoinsService purchasedCoinsService, PurchasedCoinsRepository purchasedCoinsRepository) {
        this.purchasedCoinsService = purchasedCoinsService;
        this.purchasedCoinsRepository = purchasedCoinsRepository;
    }

    @GetMapping
    public List<PurchasedCoins> getAllPurchasedCoins() {
        return purchasedCoinsService.getAllPurchasedCoins();
    }

    @PostMapping
    public PurchasedCoins addPurchasedCoins(@RequestBody PurchasedCoins purchasedCoins) {
        return purchasedCoinsService.addPurchasedCoins(purchasedCoins);
    }

    @PutMapping("/{id}")
    public PurchasedCoins updatePurchasedCoins(@PathVariable String id, @RequestBody PurchasedCoinsDTO purchasedCoinsDTO) {
        return purchasedCoinsService.updatePurchasedCoins(id, purchasedCoinsDTO);
    }

    @GetMapping("/{id}")
    public PurchasedCoins getPurchasedCoinsById(@PathVariable String id) {
        return purchasedCoinsService.getPurchasedCoinsById(id);
    }

    @DeleteMapping("/{id}")
    public PurchasedCoins deletePurchasedCoins(@PathVariable String id) {
        return purchasedCoinsService.deletePurchasedCoins(id);
    }

}

