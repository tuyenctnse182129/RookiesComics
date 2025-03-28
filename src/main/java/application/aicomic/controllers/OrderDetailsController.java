package application.aicomic.controllers;

import application.aicomic.dataAccess.CommentsDTO;
import application.aicomic.dataAccess.OrderDetailsDTO;
import application.aicomic.enums.OrdersEnums;
import application.aicomic.models.Comments;
import application.aicomic.models.OrderDetails;
import application.aicomic.models.Orders;
import application.aicomic.repositories.OrderDetailsRepository;
import application.aicomic.repositories.OrdersRepository;
import application.aicomic.services.OrderDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/orders/orderDetail")
@RestController
public class OrderDetailsController {
    @Autowired
    private OrderDetailsService orderDetailsService;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @GetMapping
    public List<OrderDetails> getAllOrderDetails() {
        return orderDetailsService.getAllOrderDetails();
    }

    @PostMapping
    public ResponseEntity<?> addOrderDetail(@RequestBody OrderDetails orderDetails) {
        Optional<Orders> existingOrder = ordersRepository.findById(orderDetails.getOrderId());

        if (existingOrder.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Order không tồn tại."));
        }

        orderDetails.setOrders(existingOrder.get()); // Gán giá trị cho orders
        orderDetailsRepository.save(orderDetails);

        return ResponseEntity.ok(orderDetails);
    }

    @PutMapping("/{id}")
    public OrderDetails updateOrderDetail(@PathVariable String id, @RequestBody OrderDetailsDTO orderDetailsDTO) {
        return orderDetailsService.updateOrderDetail(id, orderDetailsDTO);
    }
    @GetMapping("/{id}")
    public OrderDetails getOrderDetailById(@PathVariable String id) {
        return orderDetailsService.getOrderDetailById(id);
    }
    @DeleteMapping("/{id}")
    public OrderDetails deleteOrderDetailsByID(@PathVariable String id) {
        return orderDetailsService.deleteOrderDetails(id);
    }

    @PostMapping("/add-list-order-details")
    public List<OrderDetails> addOrderDetailsList(@RequestBody List<OrderDetails> orderDetailsList) {
        return orderDetailsService.addListOfOrderDetails(orderDetailsList);
    }

}
