package application.aicomic.controllers;

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

    @GetMapping
    public List<Orders> getAllOrders() {
        return ordersService.getAllOrders();
    }

    @PostMapping
    public Orders getOrdersById(@PathVariable String ordersId) {
        return ordersService.getOrdersById(ordersId);
    }

    @PostMapping("/new-order")
    public Orders createOrders(@RequestBody Orders orders) {
        return ordersService.saveOrders(orders);
    }

    @PutMapping("/{updateId}")
    public Orders updateOrders(@PathVariable String id, @RequestBody OrdersDTO ordersDTO) {
        return ordersService.updateOrders(id, ordersDTO);
    }

    @DeleteMapping("/{deleteId}")
    public Orders deleteOrders(@PathVariable String id) {
        return ordersService.deleteOrders(id);
    }
}
