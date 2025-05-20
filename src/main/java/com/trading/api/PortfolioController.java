package com.trading.api;

import com.trading.PortfolioRequest;
import com.trading.repository.BuyingPowerEntity;
import com.trading.repository.BuyingPowerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolios")
public class PortfolioController {

    @Autowired
    private BuyingPowerRepository buyingPowerRepository;

    @PostMapping
    public ResponseEntity<?> createPortfolio(@RequestBody PortfolioRequest request) {
        if (buyingPowerRepository.existsById(request.getPortfolioId())) {
            return ResponseEntity.badRequest().body("Portfolio already exists");
        }

        BuyingPowerEntity entity = new BuyingPowerEntity(request.getPortfolioId(), request.getAmount());
        buyingPowerRepository.save(entity);

        return ResponseEntity.ok(entity);
    }

}
