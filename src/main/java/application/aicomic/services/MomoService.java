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
    private final WalletsService walletsService;

    @Autowired
    public MomoService(MomoConfig momoConfig, MomoRepository momoRepository, PurchasedCoinsService purchasedCoinsService, WalletsService walletsService) {
        this.momoConfig = momoConfig;
        this.momoRepository = momoRepository;
        this.purchasedCoinsService = purchasedCoinsService;
        this.walletsService = walletsService;
    }

    public CreateMomoResponse createQR(String price, String coin, String userId, String returnUrl, String ipnUrl) {
        log.info("Tạo QR cho đơn hàng với giá: {}", price);

        String orderId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán đơn hàng với số coin: " + coin;
        String requestId = UUID.randomUUID().toString();
        String extraData = coin; // Truyền coin vào extraData
        long amount = Long.parseLong(price);
        long numberOfCoin = Long.parseLong(coin);

        // Tạo chữ ký bảo mật
        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                momoConfig.getAccessKey(), amount, extraData, ipnUrl, orderId, orderInfo,
                momoConfig.getPartnerCode(), returnUrl, requestId, momoConfig.getRequestType());

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
                .ipnUrl(ipnUrl)
                .redirectUrl(returnUrl)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .extraData(extraData) // Chứa số coin
                .amount(amount)
                .signature(prettySignature)
                .lang("vi")
                .build();

        CreateMomoResponse response = momoRepository.createMomoQR(request);

        // Lưu thông tin giao dịch vào DB
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

        // Lấy thông tin từ DB theo orderId
        PurchasedCoins purchasedCoins = purchasedCoinsService.findByOrderId(ipnResponse.getOrderId());
        if (purchasedCoins == null) {
            log.error("Không tìm thấy giao dịch với orderId: {}", ipnResponse.getOrderId());
            return false;
        }

        long numberOfCoin = (long) purchasedCoins.getNumberOfCoin();
        String userId = purchasedCoins.getUserId();

        purchasedCoinsService.updateStatus(ipnResponse.getOrderId(), status);

        if (status == PurchasedCoinsEnums.PAID) {
            walletsService.updateBalance(userId, numberOfCoin);
        }

        log.info("Cập nhật số dư cho user {} với số coin {}", userId, numberOfCoin);
        return true;
    }



//    public boolean handleMomoIPN(MomoIPNResponse ipnResponse) {
//        log.info("Nhận IPN từ MoMo: {}", ipnResponse);
//
//        if (ipnResponse == null || ipnResponse.getOrderId() == null) {
//            log.error("IPN không hợp lệ, thiếu orderId");
//            return false;
//        }
//
//        String statusCode = String.valueOf(ipnResponse.getResultCode());
//        PurchasedCoinsEnums status = statusCode.equals("0") ? PurchasedCoinsEnums.PAID : PurchasedCoinsEnums.CANCELED;
//
//        // Lấy userId từ IPN response
//        String userId = ipnResponse.getUserId();
//        if (userId == null || userId.isBlank()) {
//            log.error("UserId không có trong IPN Response");
//            return false;
//        }
//
//        purchasedCoinsService.createPurchasedCoins(
//                ipnResponse.getOrderId(),
//                "Thanh toán đơn hàng",
//                momoConfig.getPartnerCode(),
//                ipnResponse.getAmount(),
//                ipnResponse.getAmount(),
//                status,
//                userId
//        );
//        log.info("Đã tạo PurchasedCoins cho user {} với số tiền {}", userId, ipnResponse.getAmount());
//
//        if (status == PurchasedCoinsEnums.PAID) {
//            walletsService.updateBalance(userId, ipnResponse.getAmount());
//        }
//        log.info("Cập nhật số dư cho user {} với số tiền {}", userId, ipnResponse.getAmount());
//
//
//        return true;
//    }

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
