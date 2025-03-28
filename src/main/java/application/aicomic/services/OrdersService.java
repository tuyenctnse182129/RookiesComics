package application.aicomic.services;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import application.aicomic.dataAccess.MonthlyRevenueDTO;
import application.aicomic.dataAccess.OrdersDTO;
import application.aicomic.dataAccess.OrdersServiceResponseDTO;
import application.aicomic.enums.OrderDetailsEnums;
import application.aicomic.enums.OrdersEnums;

import application.aicomic.enums.TransactionsEnums;
import application.aicomic.enums.WalletType;
import application.aicomic.models.*;
import application.aicomic.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import application.aicomic.mapper.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private Mapper mapper;
    private UsersRepository usersRepository;

    @Autowired
    private ChaptersRepository chaptersRepository;

    @Autowired
    private ComicsRepository comicsRepository;

    @Autowired
    private WalletsRepository walletsRepository;

    @Autowired
    private WalletsService walletsService;

    @Autowired
    private TransactionsRepository transactionsRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    public Orders getOrdersById(String ordersId) {
        return ordersRepository.findById(ordersId).orElse(null);
    }

    public Orders saveOrders(Orders orders) {
        return ordersRepository.save(orders);
    }

    public OrdersServiceResponseDTO getById(String id) {
        Optional<Orders> ordersOptional = ordersRepository.findById(id);
        if (ordersOptional.isPresent()) {
            Orders orders = ordersOptional.get();
            return new OrdersServiceResponseDTO(true, "Orders found.", Collections.singletonList(orders));
        }
        return new OrdersServiceResponseDTO(false, "No orders found for the given orders ID.", Collections.emptyList());
    }

    public Orders addOrders(Orders orders) {
        return ordersRepository.save(orders);
    }

    public Orders updateOrders(String id, OrdersDTO ordersDTO) {
        Orders orders = ordersRepository.findById(id).orElseThrow(() -> new RuntimeException("Orders not found"));
        mapper.updateOrders(orders, ordersDTO);
        return ordersRepository.save(orders);
    }

    public Orders deleteOrders(String id) {
        Optional<Orders> x = ordersRepository.findById(id);
        if (x.isPresent()) {
            Orders order = x.get();
            order.setStatus(OrdersEnums.CANCELLED.getOrder_status());
            return ordersRepository.save(order);
        }
        return null;
    }

    @Transactional
    public Map<String, String> updateOrderStatus(String orderId, byte newStatusByte) {
        Optional<Orders> orderOpt = ordersRepository.findById(orderId);
        if (orderOpt.isEmpty()) return null;

        Orders order = orderOpt.get();
        OrdersEnums currentStatus = OrdersEnums.fromOrderStatus(order.getStatus());
        OrdersEnums newStatus = OrdersEnums.fromOrderStatus(newStatusByte);

        // Kiểm tra trạng thái hợp lệ
        if (!isValidStatusTransition(currentStatus, newStatus)) return null;

        double orderAmount = order.getTotalPrice();
        String userId = order.getUserId();

        // Nếu trạng thái mới là COMPLETED, trừ tiền từ ví
        Wallets wallet = null;
        if (newStatus == OrdersEnums.COMPLETED) {
            wallet = walletsService.getAvailableWallet(userId, orderAmount);
            if (wallet == null) return null;

            boolean walletUpdated = walletsService.updateWalletBalance(wallet, orderAmount);
            if (!walletUpdated) return null;
        }

        // Lock OrderDetails khi trạng thái thay đổi từ UNORDERED
        if (currentStatus == OrdersEnums.UNORDERED && newStatus != OrdersEnums.UNORDERED) {
            lockOrderDetails(order.getOrderId());
        }

        order.setStatus(newStatus.getOrder_status());
        ordersRepository.save(order);

        Map<String, String> result = new HashMap<>();
        result.put("orderId", orderId);
        if (wallet != null) {
            result.put("walletId", wallet.getWalletId());
        }
        return result;
    }


    private void distributePayments(String orderId) {
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderId(orderId);
        final double REWARD_AMOUNT = 699; // Fixed reward per chapter
        double totalOrderPrice = 0; // Total for BUYING_STORIES transaction

        for (OrderDetails orderDetail : orderDetailsList) {
            totalOrderPrice += orderDetail.getPrice(); // Sum up prices for BUYING_STORIES

            // Fetch chapter -> comic -> user (comic creator)
            Chapters chapter = chaptersRepository.findById(orderDetail.getChapterId())
                    .orElseThrow(() -> new RuntimeException("Chapter not found for OrderDetail ID: " + orderDetail.getOrderDetailId()));

            Comics comic = comicsRepository.findById(chapter.getComicId())
                    .orElseThrow(() -> new RuntimeException("Comic not found for Chapter ID: " + chapter.getChapterId()));

            Users comicCreator = usersRepository.findById(comic.getUserId())
                    .orElseThrow(() -> new RuntimeException("Comic creator not found for Comic ID: " + comic.getComicId()));

            // Find existing MAIN wallet for the comic creator
            Optional<Wallets> mainWalletOpt = walletsRepository.findByUserAndType(comicCreator, WalletType.MAIN);

            if (mainWalletOpt.isPresent()) {
                Wallets mainWallet = mainWalletOpt.get();
                mainWallet.setBalance(mainWallet.getBalance() + REWARD_AMOUNT); // Add earnings
                mainWallet.setUpdatedDate(LocalDateTime.now());
                walletsRepository.save(mainWallet);

                // Create a PAY_FEE transaction per chapter
                createTransaction(orderId, TransactionsEnums.Type.PAY_FEE, REWARD_AMOUNT, mainWallet.getWalletId());
            } else {
                logger.error("MAIN wallet not found for user ID: " + comicCreator.getUserId());
                throw new RuntimeException("MAIN wallet not found for user ID: " + comicCreator.getUserId());
            }
        }

        // Create a single BUYING_STORIES transaction for the total price
        createTransaction(orderId, TransactionsEnums.Type.BUYING_STORIES, totalOrderPrice, null);
    }

    private void createTransaction(String orderId, TransactionsEnums.Type transactionType, double amount, String walletId) {
        Transactions transaction = new Transactions();
        transaction.setOrderId(orderId);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setStatus(TransactionsEnums.PAID.getValue()); // Mark as PAID
        transaction.setType(transactionType.getValue()); // Store enum value

        if (walletId != null) {
            transaction.setWalletId(walletId);
        }

        transactionsRepository.save(transaction); // Save transaction
    }

    // Hàm khóa OrderDetails
    private void lockOrderDetails(String orderId) {
        List<OrderDetails> orderDetailsList = orderDetailsRepository.findByOrderId(orderId);
        for (OrderDetails detail : orderDetailsList) {
            detail.setStatus(OrderDetailsEnums.INACTIVE.getValue()); // Hoặc có thể tạo thêm trạng thái riêng
        }
        orderDetailsRepository.saveAll(orderDetailsList);
    }

    private boolean isValidStatusTransition(OrdersEnums currentStatus, OrdersEnums newStatus) {
        Map<OrdersEnums, List<OrdersEnums>> validTransitions = new HashMap<>();

        validTransitions.put(OrdersEnums.UNORDERED, List.of(OrdersEnums.PENDING, OrdersEnums.CANCELLED, OrdersEnums.COMPLETED));
        validTransitions.put(OrdersEnums.PENDING, List.of(OrdersEnums.COMPLETED, OrdersEnums.CANCELLED));
        validTransitions.put(OrdersEnums.COMPLETED, List.of());
        validTransitions.put(OrdersEnums.CANCELLED, List.of());

        return validTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus);
    }

}
