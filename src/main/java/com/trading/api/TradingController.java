package com.trading.api;

import com.trading.*;
import com.trading.repository.OrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * This is the entry point for all trading operations.
 */
@RestController
@RequestMapping("/orders")
public class TradingController {

    @Autowired
    private TradingService tradingService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            OrderEntity order = tradingService.createOrder(orderRequest);
            return ResponseEntity.ok(new OrderResponse(
                    order.getId(),
                    order.getPortfolioId(),
                    order.getIsin(),
                    order.getStatus(),
                    order.getSide(),
                    order.getQuantity(),
                    order.getPrice()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new TradingErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        try {
            OrderEntity order = tradingService.getOrder(id);

            return ResponseEntity.ok(new OrderResponse(
                    order.getId(),
                    order.getPortfolioId(),
                    order.getIsin(),
                    order.getStatus(),
                    order.getSide(),
                    order.getQuantity(),
                    order.getPrice()));
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new TradingErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        try {
            OrderEntity order = tradingService.cancelOrder(id);
            return ResponseEntity.ok(new OrderResponse(
                    order.getId(),
                    order.getPortfolioId(),
                    order.getIsin(),
                    order.getStatus(),
                    order.getSide(),
                    order.getQuantity(),
                    order.getPrice()
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TradingErrorResponse(HttpStatus.NOT_FOUND.value(), "Order not found"));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TradingErrorResponse(HttpStatus.BAD_REQUEST.value(), "Order cannot be cancelled"));
        }
    }

}
