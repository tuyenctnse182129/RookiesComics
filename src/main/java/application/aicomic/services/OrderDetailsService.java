package application.aicomic.services;

import application.aicomic.dataAccess.CommentsDTO;
import application.aicomic.dataAccess.OrderDetailsDTO;
import application.aicomic.enums.CommentsEnums;
import application.aicomic.enums.OrderDetailsEnums;
import application.aicomic.enums.OrdersEnums;
import application.aicomic.mapper.Mapper;
import application.aicomic.models.Comments;
import application.aicomic.models.OrderDetails;
import application.aicomic.models.Orders;
import application.aicomic.repositories.OrderDetailsRepository;
import application.aicomic.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailsService {
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private Mapper mapper;

    public List<OrderDetails> getAllOrderDetails() {
        return orderDetailsRepository.findAll();
    }

    public OrderDetails getOrderDetailById(String id) {
        return orderDetailsRepository.findById(id).get();
    }

    public OrderDetails addOrderDetail(OrderDetails orderDetails) {
        // Lấy order từ order_id
        Orders order = ordersRepository.findById(orderDetails.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Kiểm tra trạng thái của order
        if (order.getStatus() != OrdersEnums.UNORDERED.getOrder_status()) {
            throw new RuntimeException("Cannot modify OrderDetails after order is placed");
        }

        return orderDetailsRepository.save(orderDetails);
    }

    public OrderDetails updateOrderDetail(String id, OrderDetailsDTO orderDetails) {
        OrderDetails existingDetail = orderDetailsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderDetail not found"));

        // Kiểm tra trạng thái của order
        Orders order = ordersRepository.findById(existingDetail.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getStatus() != OrdersEnums.UNORDERED.getOrder_status()) {
            throw new RuntimeException("Cannot modify OrderDetails after order is placed");
        }

        // Cập nhật order details
        mapper.updateOrderDetails(existingDetail, orderDetails);
        return orderDetailsRepository.save(existingDetail);
    }

    public OrderDetails deleteOrderDetails(String id) {
        Optional<OrderDetails> orderDetailsOption = orderDetailsRepository.findById(id);
        if (orderDetailsOption.isPresent()) {
            OrderDetails detail = orderDetailsOption.get();

            // Kiểm tra trạng thái của order
            Orders order = ordersRepository.findById(detail.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            if (order.getStatus() != OrdersEnums.UNORDERED.getOrder_status()) {
                throw new RuntimeException("Cannot delete OrderDetails after order is placed");
            }

            detail.setStatus(OrderDetailsEnums.INACTIVE.getValue());
            return orderDetailsRepository.save(detail);
        }
        return null;
    }

    public List<OrderDetails> addListOfOrderDetails(List<OrderDetails> orderDetailsList) {
        for (OrderDetails detail : orderDetailsList) {
            Orders order = ordersRepository.findById(detail.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            if (order.getStatus() != OrdersEnums.UNORDERED.getOrder_status()) {
                throw new RuntimeException("Cannot modify OrderDetails after order is placed");
            }
        }
        return orderDetailsRepository.saveAll(orderDetailsList);
    }
}
