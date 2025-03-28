package application.aicomic.controllers;

import application.aicomic.dataAccess.UpdateOrderStatusRequest;
import application.aicomic.repositories.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import application.aicomic.services.OrdersService;
import application.aicomic.dataAccess.OrdersDTO;
import application.aicomic.models.Orders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/orders")
@RestController
public class OrdersController {
    private OrdersService ordersService;
    private OrdersRepository ordersRepository;

    public OrdersController(OrdersService ordersService, OrdersRepository ordersRepository) {
        this.ordersService = ordersService;
        this.ordersRepository = ordersRepository;
    }

    @GetMapping
    public List<Orders> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @GetMapping("/{id}")
    public Orders getOrdersById(@PathVariable String ordersId) {
        return ordersService.getOrdersById(ordersId);
    }


    @PostMapping
    public ResponseEntity<?> addOrder(@RequestBody Orders orders) {
        System.out.println("Received Orders: " + orders);

        if (orders.getOrderDetails() == null || orders.getOrderDetails().isEmpty()) {
            return ResponseEntity.badRequest().body("orderDetails is null or empty!");
        }

        Orders savedOrder = ordersService.saveOrders(orders);
        return ResponseEntity.ok(savedOrder);
    }

    @PutMapping("/{id}")
    public Orders updateOrders(@PathVariable String id, @RequestBody OrdersDTO ordersDTO) {
        return ordersService.updateOrders(id, ordersDTO);
    }

    @DeleteMapping("/{id}")
    public Orders deleteOrders(@PathVariable String id) {
        return ordersService.deleteOrders(id);
    }

    @PostMapping("/find-by-user-and-status")
    public ResponseEntity<?> getOrdersByUserIdAndStatus(@RequestBody UpdateOrderStatusRequest request) {
        Orders order = ordersService.getOrdersByUserIdAndStatus(request.getOrderId(), request.getNewStatusByte());
        if (order != null) {
            return ResponseEntity.ok(order);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy đơn hàng");
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(@RequestBody UpdateOrderStatusRequest request) {
        Map<String, String> result = ordersService.updateOrderStatus(request.getOrderId(), request.getNewStatusByte());

        Map<String, Object> response = new HashMap<>();
        boolean isUpdated = result != null && result.containsKey("orderId");

        response.put("success", isUpdated);
        response.put("message", isUpdated ? "Cập nhật trạng thái đơn hàng thành công" : "Không thể cập nhật trạng thái đơn hàng");
        if (isUpdated) {
            response.put("orderId", result.get("orderId"));
            if (result.containsKey("walletId")) {
                response.put("walletId", result.get("walletId"));
            }
        }

        return isUpdated ? ResponseEntity.ok(response) : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
