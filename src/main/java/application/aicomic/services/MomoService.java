package application.aicomic.services;

import application.aicomic.config.MomoConfig;
import application.aicomic.dataAccess.CreateMomoRequest;
import application.aicomic.dataAccess.CreateMomoResponse;
import application.aicomic.dataAccess.MomoIPNResponse;
import application.aicomic.enums.PurchasedCoinsEnums;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.repositories.MomoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@Slf4j
public class MomoService {
    private final MomoConfig momoConfig;
    private final MomoRepository momoRepository;
    private final PurchasedCoinsService purchasedCoinsService;

    @Autowired
    public MomoService(MomoConfig momoConfig, MomoRepository momoRepository, PurchasedCoinsService purchasedCoinsService) {
        this.momoConfig = momoConfig;
        this.momoRepository = momoRepository;
        this.purchasedCoinsService = purchasedCoinsService;
    }

    public CreateMomoResponse createQR(String price, String coin, String userId) {
        log.info("Tạo QR cho đơn hàng với giá: {}", price);

        String orderId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán đơn hàng với số coin: " + coin;
        String requestId = UUID.randomUUID().toString();
        String extraData = "Không có khuyến mãi";
        long amount = Long.parseLong(price);
        long numberOfCoin = Long.parseLong(coin);

        // Tạo chữ ký bảo mật
        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                momoConfig.getAccessKey(), amount, extraData, momoConfig.getIpnUrl(), orderId, orderInfo,
                momoConfig.getPartnerCode(), momoConfig.getReturnUrl(), requestId, momoConfig.getRequestType());

        String prettySignature;
        try {
            prettySignature = signHmacSHA256(rawSignature, momoConfig.getSecretKey());
        } catch (Exception e) {
            log.error("Lỗi khi tạo chữ ký: ", e);
            return null;
        }

        if (prettySignature.isBlank()) {
            log.error("Chữ ký tạo ra bị trống");
            return null;
        }

        CreateMomoRequest request = CreateMomoRequest.builder()
                .partnerCode(momoConfig.getPartnerCode())
                .requestType(momoConfig.getRequestType())
                .ipnUrl(momoConfig.getIpnUrl())
                .redirectUrl(momoConfig.getReturnUrl())
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .extraData(extraData)
                .amount(amount)
                .signature(prettySignature)
                .lang("vi")
                .build();

        CreateMomoResponse response = momoRepository.createMomoQR(request);

        // Lưu giao dịch vào database với trạng thái "NOT_PAID" (đang chờ thanh toán)
        purchasedCoinsService.createPurchasedCoins(orderId, orderInfo, momoConfig.getPartnerCode(),
                amount, numberOfCoin, PurchasedCoinsEnums.NOT_PAID, userId);

        return response;
    }

    public boolean handleMomoIPN(MomoIPNResponse ipnResponse) {
        log.info("Nhận IPN từ MoMo: {}", ipnResponse);

        if (ipnResponse == null || ipnResponse.getOrderId() == null) {
            log.error("IPN không hợp lệ, thiếu orderId");
            return false;
        }

        String statusCode = String.valueOf(ipnResponse.getResultCode());
        PurchasedCoinsEnums status = statusCode.equals("0") ? PurchasedCoinsEnums.PAID : PurchasedCoinsEnums.CANCELED;

        // Lấy userId từ IPN response
        String userId = ipnResponse.getUserId();
        if (userId == null || userId.isBlank()) {
            log.error("UserId không có trong IPN Response");
            return false;
        }

        purchasedCoinsService.createPurchasedCoins(
                ipnResponse.getOrderId(),
                "Thanh toán đơn hàng",
                momoConfig.getPartnerCode(),
                ipnResponse.getAmount(),
                ipnResponse.getAmount(),
                status,
                userId
        );

        return true;
    }


    private String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretkey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretkey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
