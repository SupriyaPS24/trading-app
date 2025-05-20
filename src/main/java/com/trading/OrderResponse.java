package com.trading;

import java.math.BigDecimal;

public class OrderResponse {

    private Long id;
    private String portfolioId;
    private String isin;
    private OrderStatus status;
    private OrderSide side;
    private BigDecimal quantity;
    private BigDecimal price;

    public OrderResponse(Long id, String portfolioId, String isin, OrderStatus status,
                             OrderSide side, BigDecimal quantity, BigDecimal price) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.isin = isin;
        this.status = status;
        this.side = side;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public Long getId() { return id; }
    public String getPortfolioId() { return portfolioId; }
    public String getIsin() { return isin; }
    public OrderStatus getStatus() { return status; }
    public OrderSide getSide() { return side; }
    public BigDecimal getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }

}
