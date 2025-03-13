package application.aicomic.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import application.aicomic.services.OrdersService;
import application.aicomic.dataAccess.OrdersDTO;
import application.aicomic.models.Orders;
import java.util.List;

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

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable String id, @RequestParam byte status) {
        boolean updated = ordersService.updateOrderStatus(id, status);
        if (updated) {
            return ResponseEntity.ok("Cập nhật trạng thái thành công");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Không thể cập nhật trạng thái đơn hàng");
    }
}
