package application.aicomic.controllers;

import application.aicomic.dataAccess.CreateMomoResponse;
import application.aicomic.dataAccess.MomoIPNResponse;
import application.aicomic.services.MomoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/momo")
public class MomoController {
    private final MomoService momoService;

    @Autowired
    public MomoController(MomoService momoService) {
        this.momoService = momoService;
    }

    @PostMapping("create")
    public ResponseEntity<CreateMomoResponse> createQR(@RequestBody Map<String, String> requestData) {
        String orderId = UUID.randomUUID().toString();
        String price = requestData.get("price");
        String coin = requestData.get("coin");
        String userId = requestData.get("userId");

        CreateMomoResponse response = momoService.createQR(price, coin, userId);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(response);
    }


    @PostMapping("/ipn-handler")
    public ResponseEntity<String> handleMomoIPN(@RequestBody MomoIPNResponse ipnResponse) {
        if (ipnResponse == null || ipnResponse.getOrderId() == null) {
            log.error("IPN không hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("IPN không hợp lệ");
        }

        log.info("Nhận IPN từ MoMo: {}", ipnResponse);

        boolean success = momoService.handleMomoIPN(ipnResponse);
        return success ? ResponseEntity.ok("Xử lý thành công")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xử lý thất bại");
    }
}
