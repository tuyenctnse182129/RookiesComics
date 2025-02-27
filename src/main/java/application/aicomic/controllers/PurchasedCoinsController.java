package application.aicomic.controllers;

import java.util.List;
import java.util.Map;

import application.aicomic.config.Config;
import application.aicomic.repositories.PurchasedCoinsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/returning")
    public ResponseEntity<String> vnpReturn(@RequestParam Map<String, String> queryParams) {
        try {
            System.out.println("🔹 VNPAY Response: " + queryParams);

            String userId = queryParams.get("userId");  // Lấy userId từ query
            String numberOfCoin = queryParams.get("numberOfCoin");
            String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
            String vnp_TxnRef = queryParams.get("vnp_TxnRef");
            String vnp_Amount = queryParams.get("vnp_Amount");
            String vnp_BankCode = queryParams.get("vnp_BankCode");
            String vnp_PayDate = queryParams.get("vnp_PayDate");
            String vnp_SecureHash = queryParams.get("vnp_SecureHash");

            if (vnp_ResponseCode == null || vnp_TxnRef == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu dữ liệu từ VNPAY");
            }

            // Kiểm tra chữ ký
            String signData = Config.hashAllFields(queryParams);
            if (!signData.equals(vnp_SecureHash)) {
                System.out.println("❌ Chữ ký không hợp lệ!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
            }

            boolean isSuccess = "00".equals(vnp_ResponseCode);
            purchasedCoinsService.savePurchasedCoinToDB(userId, numberOfCoin, vnp_TxnRef, vnp_Amount, vnp_BankCode, vnp_PayDate, isSuccess);

            return ResponseEntity.ok(isSuccess ? "✅ Payment successful" : "❌ Payment failed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý giao dịch");
        }
    }
}

