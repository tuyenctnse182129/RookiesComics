package application.aicomic.services;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import application.aicomic.dataAccess.MonthlyRevenueDTO;
import application.aicomic.dataAccess.OrdersDTO;
import application.aicomic.dataAccess.OrdersServiceResponseDTO;
import application.aicomic.enums.OrderDetailsEnums;
import application.aicomic.enums.OrdersEnums;

import application.aicomic.models.OrderDetails;
import application.aicomic.repositories.OrderDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import application.aicomic.models.Orders;
import application.aicomic.repositories.OrdersRepository;
import application.aicomic.mapper.Mapper;

@Service
public class OrdersService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private Mapper mapper;

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

    public boolean updateOrderStatus(String orderId, byte newStatusByte) {
        Optional<Orders> orderOpt = ordersRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Orders order = orderOpt.get();
            OrdersEnums currentStatus = OrdersEnums.fromOrderStatus(order.getStatus());
            OrdersEnums newStatus = OrdersEnums.fromOrderStatus(newStatusByte);

            // Kiểm tra trạng thái hợp lệ
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                return false; // Tránh cập nhật trạng thái sai logic
            }

            // Khi chuyển từ UNORDERED sang trạng thái khác, khóa OrderDetails
            if (currentStatus == OrdersEnums.UNORDERED && newStatus != OrdersEnums.UNORDERED) {
                lockOrderDetails(order.getOrderId());
            }

            order.setStatus(newStatus.getOrder_status());
            ordersRepository.save(order);
            return true;
        }
        return false;
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

        validTransitions.put(OrdersEnums.UNORDERED, List.of(OrdersEnums.PENDING, OrdersEnums.CANCELLED));
        validTransitions.put(OrdersEnums.PENDING, List.of(OrdersEnums.COMPLETED, OrdersEnums.CANCELLED));
        validTransitions.put(OrdersEnums.COMPLETED, List.of());
        validTransitions.put(OrdersEnums.CANCELLED, List.of());

        return validTransitions.getOrDefault(currentStatus, List.of()).contains(newStatus);
    }

}
