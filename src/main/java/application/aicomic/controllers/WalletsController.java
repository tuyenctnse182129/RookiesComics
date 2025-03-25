package application.aicomic.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.models.Wallets;
import java.util.List;
import application.aicomic.services.WalletsService;

@RestController
@RequestMapping("/wallets")
public class WalletsController {
    @Autowired
    private WalletsService walletsService;

    @GetMapping
    public List<Wallets> getAllWallets() {
        return walletsService.getAllWallets();
    }

    @GetMapping("/{id}")
    public Wallets getWalletById(@PathVariable String walletsId) {
        return walletsService.getWalletById(walletsId);
    }

    @PostMapping("/new-wallet")
    public Wallets createWallets(@RequestBody Wallets wallets) {
        return walletsService.saveWallets(wallets);
    }

    @PutMapping("/{id}")
    public Wallets updateWallets(@PathVariable String id, @RequestBody WalletsDTO walletsDTO) {
        return walletsService.updateWallets(id, walletsDTO);
    }

    @GetMapping("/{userId}/main-wallet")
    public Wallets getMainWallet(@PathVariable String userId) {
        return walletsService.getMainWalletByUserId(userId);
    }

    @GetMapping("/{userId}/promotion-wallet")
    public Wallets getPromotionWallet(@PathVariable String userId) {
        return walletsService.getPromotionWalletByUserId(userId);
    }

}
