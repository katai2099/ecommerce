package com.web.ecommerce.controller;

import com.web.ecommerce.dto.PaginationResponse;
import com.web.ecommerce.dto.order.OrderDTO;
import com.web.ecommerce.dto.order.OrderDetailDTO;
import com.web.ecommerce.model.OrderAnalytic;
import com.web.ecommerce.model.SaleAnalytic;
import com.web.ecommerce.service.OrderService;
import com.web.ecommerce.specification.order.OrderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<PaginationResponse<OrderDetailDTO>> getOrders(@ModelAttribute OrderFilter filter) {
        return ResponseEntity.ok(orderService.getOrders(filter));
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
    public ResponseEntity<String> updateOrderStatus(@PathVariable UUID orderId,
                                                    @RequestParam String status) {
        String newStatus = orderService.updateOrderStatus(orderId,status);
        return ResponseEntity.ok("Order status update to " + newStatus + " completed");
    }

    @GetMapping("/recent-order")
    public ResponseEntity<List<OrderDetailDTO>> getRecentOrders() {
        return ResponseEntity.ok(orderService.getRecentOrders());
    }

    @GetMapping("/order-analytic")
    public ResponseEntity<OrderAnalytic> getOrderAnalytic() {
        return ResponseEntity.ok(orderService.getOrderAnalytic());
    }

    @GetMapping("/sales-analytic")
    public ResponseEntity<SaleAnalytic> getSalesAnalytic() {
        return ResponseEntity.ok(orderService.getSalesAnalytic());
    }


}
