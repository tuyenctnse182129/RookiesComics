package application.aicomic.controllers;

import application.aicomic.dataAccess.UpdateOrderStatusRequest;
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

    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
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
    public Orders createOrders(@RequestBody Orders orders) {
        return ordersService.saveOrders(orders);
    }

    @PutMapping("/{id}")
    public Orders updateOrders(@PathVariable String id, @RequestBody OrdersDTO ordersDTO) {
        return ordersService.updateOrders(id, ordersDTO);
    }

    @DeleteMapping("/{id}")
    public Orders deleteOrders(@PathVariable String id) {
        return ordersService.deleteOrders(id);
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
