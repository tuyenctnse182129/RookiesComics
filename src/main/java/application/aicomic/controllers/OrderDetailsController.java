package application.aicomic.controllers;

import application.aicomic.dataAccess.CommentsDTO;
import application.aicomic.dataAccess.OrderDetailsDTO;
import application.aicomic.models.Comments;
import application.aicomic.models.OrderDetails;
import application.aicomic.services.OrderDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/orders/orderDetail")
@RestController
public class OrderDetailsController {
    private OrderDetailsService orderDetailsService;

    @GetMapping
    public List<OrderDetails> getAllOrderDetails() {
        return orderDetailsService.getAllOrderDetails();
    }

    @PostMapping
    public OrderDetails addOrderDetail(@RequestBody OrderDetails orderDetails) {
        return orderDetailsService.addOrderDetail(orderDetails);
    }

    @PutMapping("/{updateId}")
    public OrderDetails updateOrderDetail(@PathVariable String id, @RequestBody OrderDetailsDTO orderDetailsDTO) {
        return orderDetailsService.updateOrderDetail(id, orderDetailsDTO);
    }
    @GetMapping("/{id}")
    public OrderDetails getOrderDetailById(@PathVariable String id) {
        return orderDetailsService.getOrderDetailById(id);
    }
    @DeleteMapping("/{deleteId}")
    public OrderDetails deleteOrderDetailsByID(@PathVariable String id) {
        return orderDetailsService.deleteOrderDetails(id);
    }

    @PostMapping("/add-list-order-details")
    public List<OrderDetails> addOrderDetailsList(@RequestBody List<OrderDetails> orderDetailsList) {
        return orderDetailsService.addListOfOrderDetails(orderDetailsList);
    }

}
