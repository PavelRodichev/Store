package by.pasha.currencyservice;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CurrencyServiceController {


    @GetMapping("/api/rates/usd")
    public Map<String, Object> getUsdRate() {

        return Map.of(
                "USD", 90.5,

                "RUB", 1,

                "EUR", 102.30
        );
    }


}
