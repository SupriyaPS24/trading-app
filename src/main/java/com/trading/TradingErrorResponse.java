package com.trading;

public class TradingErrorResponse {

    private int status;
    private String message;

    public TradingErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
