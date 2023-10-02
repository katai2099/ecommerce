package com.web.ecommerce.controller;

import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.order.OrderDTO;
import com.web.ecommerce.dto.order.OrderDetailDTO;
import com.web.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public ResponseEntity<PaginationResponse<OrderDTO>> getOrders(@RequestParam(required = false, defaultValue = "1") int page,
                                                                  @RequestParam(required = false) String status) {
        return ResponseEntity.ok(orderService.getOrders(page));
    }

    @GetMapping("/user/")
    public ResponseEntity<PaginationResponse<OrderDTO>> getUserOrders(@RequestParam(required = false, defaultValue = "1") int page) {
        return ResponseEntity.ok(orderService.getUserOrders(page));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailDTO> getOrderDetail(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable Long orderId) {
        String newStatus = orderService.updateOrderStatus(orderId);
        return ResponseEntity.ok("Order status update to " + newStatus + " completed");
    }


}
