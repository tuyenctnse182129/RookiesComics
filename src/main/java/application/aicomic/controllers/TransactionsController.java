package application.aicomic.controllers;

import application.aicomic.config.Config;
import application.aicomic.dataAccess.TransactionsDTO;
import application.aicomic.enums.TransactionsEnums;
import application.aicomic.enums.OrdersEnums;
import application.aicomic.models.Transactions;
import application.aicomic.repositories.TransactionsRepository;
import application.aicomic.services.OrdersService;
import application.aicomic.services.TransactionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RequestMapping("/transaction")
@RestController
public class TransactionsController {
    @Autowired
    private TransactionsService transactionsService;
    @Autowired
    private TransactionsRepository transactionsRepository;
    @Autowired
    private OrdersService orderService;

    // Inject TransactionsService
    public TransactionsController(TransactionsService transactionsService, TransactionsRepository transactionsRepository, OrdersService orderService) {
        this.transactionsService = transactionsService;
        this.transactionsRepository = transactionsRepository;
        this.orderService = orderService;
    }

    @GetMapping("/getAll")
    public List<Transactions> getAllTransactions() {
        return transactionsService.getAllTransactions();
    }

    @PostMapping("/post")
    public Transactions addTransaction(@RequestBody Transactions transactions) {
        return transactionsService.addTransaction(transactions);
    }

    @PutMapping("/update")
    public Transactions updateTransaction(@PathVariable String id, @RequestBody TransactionsDTO transactionsDTO) {
        return transactionsService.updateTransaction(id, transactionsDTO);
    }

    @GetMapping("/getById")
    public Transactions getTransactionById(@PathVariable String id) {
        return transactionsService.getTransactionById(id);
    }

    @DeleteMapping("/delete")
    public Transactions deleteTransaction(@PathVariable String id) {
        return transactionsService.deleteTransaction(id);
    }

    @GetMapping("/return")
    public ResponseEntity<String> vnpReturn(@RequestParam Map<String, String> queryParams, @RequestParam String orderId) {
        try {
            System.out.println("🔹 VNPAY Response: " + queryParams);

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
            // Lưu giao dịch
            Transactions transaction = new Transactions();
            transaction.setOrderId(orderId);
            transaction.setTransactionCode(vnp_TxnRef);
            transaction.setAmount(Double.parseDouble(vnp_Amount) / 100);
            transaction.setBankName(vnp_BankCode);
            transaction.setTransactionTime(LocalDateTime.parse(vnp_PayDate, DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            transaction.setStatus(isSuccess ? TransactionsEnums.PAID.getValue() : TransactionsEnums.NOT_PAID.getValue());

            transactionsRepository.save(transaction);

            // Cập nhật trạng thái đơn hàng
            if (isSuccess) {
                boolean updated = orderService.updateOrderStatus(orderId, OrdersEnums.PENDING.getOrder_status());
                if (!updated) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cập nhật trạng thái đơn hàng thất bại");
                }
            }

            return ResponseEntity.ok(isSuccess ? "✅ Payment successful" : "❌ Payment failed");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi xử lý giao dịch");
        }
    }
}