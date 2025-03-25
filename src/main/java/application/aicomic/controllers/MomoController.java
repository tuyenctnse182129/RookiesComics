package application.aicomic.controllers;

import application.aicomic.config.MomoConfig;
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
    private final MomoConfig momoConfig;


    @Autowired
    public MomoController(MomoService momoService , MomoConfig momoConfig) {
        this.momoService = momoService;
        this.momoConfig = momoConfig;
    }

    @PostMapping("/create")
    public ResponseEntity<CreateMomoResponse> createQR(@RequestBody Map<String, String> requestData) {
        String price = requestData.get("price");
        String coin = requestData.get("coin");
        String userId = requestData.get("userId");
        String returnUrl = requestData.getOrDefault("returnUrl", momoConfig.getReturnUrl());
        String ipnUrl = requestData.getOrDefault("ipnUrl", momoConfig.getIpnUrl());

        CreateMomoResponse response = momoService.createQR(price, coin, userId, returnUrl, ipnUrl);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        return ResponseEntity.ok(response);
    }



    @PostMapping("/ipn-handler")
    public ResponseEntity<String> handleMomoIPN(@RequestBody MomoIPNResponse ipnResponse) {
        log.info("Gọi API handleMomoIPN");
        log.info("Nhận IPN từ MoMo: {}", ipnResponse);
        if (ipnResponse == null || ipnResponse.getOrderId() == null) {
            log.error("IPN không hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("IPN không hợp lệ");
        }

        boolean success = momoService.handleMomoIPN(ipnResponse);
        return success ? ResponseEntity.ok("Xử lý thành công")
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xử lý thất bại");
    }
}
