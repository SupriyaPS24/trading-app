package com.trading;

import java.math.BigDecimal;


public class OrderRequest {

    private String portfolioId;
        private String isin;
    private OrderSide side;
    private BigDecimal quantity;

    public String getPortfolioId() {
        return portfolioId;
    }

    public String getIsin() {
        return isin;
    }

    public OrderSide getSide() {
        return side;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public OrderRequest() {
    }

    public OrderRequest(String portfolioId, String isin, OrderSide side, BigDecimal quantity) {
        this.portfolioId = portfolioId;
        this.isin = isin;
        this.side = side;
        this.quantity = quantity;
    }
}
