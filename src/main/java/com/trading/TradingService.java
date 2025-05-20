package com.trading;

import com.trading.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
public class TradingService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private BuyingPowerRepository buyingPowerRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private MarketDataService marketDataService;

    @Transactional
    public OrderEntity createOrder(OrderRequest request) {
        OrderSide side = request.getSide();
        BigDecimal price = marketDataService.getPrice(request.getIsin());
        BigDecimal quantity = request.getQuantity();
        BigDecimal total = price.multiply(quantity);

        String portfolioId = request.getPortfolioId();

        BuyingPowerEntity existingPower = buyingPowerRepository.findById(portfolioId)
                .orElse(new BuyingPowerEntity(portfolioId, new BigDecimal("5000.00")));

        InventoryEntityId inventoryId = new InventoryEntityId(portfolioId, request.getIsin());
        InventoryEntity inventory = inventoryRepository.findById(inventoryId)
                .orElse(new InventoryEntity(portfolioId, request.getIsin(), BigDecimal.ZERO));

        if (side == OrderSide.BUY) {
            if (existingPower.getAmount().compareTo(total) < 0) {
                throw new IllegalArgumentException("Insufficient buying power");
            }

            buyingPowerRepository.save(new BuyingPowerEntity(portfolioId, existingPower.getAmount().subtract(total)));

            inventoryRepository.save(new InventoryEntity(portfolioId, request.getIsin(), inventory.getQuantity().add(quantity)));

        } else if (side == OrderSide.SELL) {
            if (inventory.getQuantity().compareTo(quantity) < 0) {
                throw new IllegalArgumentException("Insufficient inventory");
            }

            inventoryRepository.save(new InventoryEntity(
                    portfolioId,
                    request.getIsin(),
                    inventory.getQuantity().subtract(quantity)
            ));

            buyingPowerRepository.save(new BuyingPowerEntity(
                    portfolioId,
                    existingPower.getAmount().add(total)
            ));
        } else {
            throw new UnsupportedOperationException("Unsupported order side: " + side);
        }

        OrderEntity order = new OrderEntity(
                portfolioId,
                request.getIsin(),
                OrderStatus.CREATED,
                side,
                quantity,
                price
        );

        return orderRepository.save(order);
    }

    public OrderEntity getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Transactional
    public OrderEntity cancelOrder(Long id) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Order not found"));

        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be cancelled.");
        }

        BigDecimal refundAmount = order.getPrice().multiply(order.getQuantity());

        BuyingPowerEntity currentPower = buyingPowerRepository.findById(order.getPortfolioId())
                .orElse(new BuyingPowerEntity(order.getPortfolioId(), new BigDecimal("5000.00")));

        InventoryEntityId inventoryId = new InventoryEntityId(order.getPortfolioId(), order.getIsin());
        InventoryEntity inventory = inventoryRepository.findById(inventoryId)
                .orElse(new InventoryEntity(order.getPortfolioId(), order.getIsin(), BigDecimal.ZERO));

        if (order.getSide() == OrderSide.BUY) {
            buyingPowerRepository.save(new BuyingPowerEntity(
                    order.getPortfolioId(),
                    currentPower.getAmount().add(refundAmount)
            ));

            inventoryRepository.save(new InventoryEntity(
                    inventory.getPortfolioId(),
                    inventory.getIsin(),
                    inventory.getQuantity().subtract(order.getQuantity())
            ));
        } else if (order.getSide() == OrderSide.SELL) {
            buyingPowerRepository.save(new BuyingPowerEntity(
                    order.getPortfolioId(),
                    currentPower.getAmount().subtract(refundAmount)
            ));

            inventoryRepository.save(new InventoryEntity(
                    inventory.getPortfolioId(),
                    inventory.getIsin(),
                    inventory.getQuantity().add(order.getQuantity())
            ));
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }
}
