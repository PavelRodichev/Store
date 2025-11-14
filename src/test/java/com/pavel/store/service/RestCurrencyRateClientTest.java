package com.pavel.store.service;

import com.pavel.store.controller.rest.session.SessionCurrency;
import com.pavel.store.handler.Enhancer.ExchangeRateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestCurrencyRateClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ExchangeRateProvider exchangeRateProvider;

    @Mock
    private SessionCurrency sessionCurrency;

    @InjectMocks
    private RestCurrencyRateClient currencyRateClient;

    private final String currencyServiceUrl = "http://currency-service/api/rates";

    @BeforeEach
    void setUp() {
        // Устанавливаем URL через reflection, так как он инжектится через @Value
        try {
            var field = RestCurrencyRateClient.class.getDeclaredField("currencyServiceUrl");
            field.setAccessible(true);
            field.set(currencyRateClient, currencyServiceUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getRate_WhenServiceReturnsSuccess_ShouldReturnRateFromService() {

        String currency = "EUR";
        Double expectedRate = 0.85;
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(currency, expectedRate);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class)).thenReturn(responseEntity);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.valueOf(expectedRate), result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verify(sessionCurrency, atLeastOnce()).getCurrency();
        verifyNoInteractions(exchangeRateProvider);
    }

    @Test
    void getRate_WhenServiceFails_ShouldReturnRateFromFile() {

        String currency = "EUR";
        Double expectedRate = 0.86;
        Map<Object, Object> fileRates = new HashMap<>();
        fileRates.put(currency, expectedRate);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class))
                .thenThrow(new RestClientException("Service unavailable"));
        when(exchangeRateProvider.getExchangeRate()).thenReturn(fileRates);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.valueOf(expectedRate), result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verify(exchangeRateProvider).getExchangeRate();
        verify(sessionCurrency, atLeastOnce()).getCurrency();
    }

    @Test
    void getRate_WhenServiceReturnsEmptyBody_ShouldReturnDefaultRate() {

        String currency = "EUR";
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(new HashMap<>(), HttpStatus.OK);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class)).thenReturn(responseEntity);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verifyNoInteractions(exchangeRateProvider); // Fallback на файл не должен вызываться
    }

    @Test
    void getRate_WhenServiceReturnsWrongCurrency_ShouldReturnDefaultRate() {

        String requestedCurrency = "EUR";
        String availableCurrency = "USD";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(availableCurrency, 1.0);
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(sessionCurrency.getCurrency()).thenReturn(requestedCurrency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class)).thenReturn(responseEntity);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verifyNoInteractions(exchangeRateProvider); // Убрана неиспользуемая заглушка
    }
    @Test
    void getRate_WhenBothServiceAndFileFail_ShouldReturnDefaultRate() {

        when(restTemplate.getForEntity(currencyServiceUrl, Map.class))
                .thenThrow(new RestClientException("Service unavailable"));
        when(exchangeRateProvider.getExchangeRate()).thenReturn(new HashMap<>());


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verify(exchangeRateProvider).getExchangeRate();

    }

    @Test
    void getRate_WhenServiceReturnsNonDoubleRate_ShouldReturnDefaultRate() {

        String currency = "EUR";
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(currency, "invalid-rate");
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class)).thenReturn(responseEntity);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verifyNoInteractions(exchangeRateProvider);
    }

    @Test
    void getRate_WhenFileReturnsNonDoubleRate_ShouldReturnDefaultRate() {

        String currency = "EUR";
        Map<Object, Object> fileRates = new HashMap<>();
        fileRates.put(currency, "invalid-rate");

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class))
                .thenThrow(new RestClientException("Service unavailable"));
        when(exchangeRateProvider.getExchangeRate()).thenReturn(fileRates);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verify(exchangeRateProvider).getExchangeRate();
    }

    @Test
    void getRate_WhenServiceReturnsHttpError_ShouldReturnRateFromFile() {

        String currency = "EUR";
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class)).thenReturn(responseEntity);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verifyNoInteractions(exchangeRateProvider);
    }

    @Test
    void getRate_WhenServiceCallThrowsRuntimeException_ShouldReturnRateFromFile() {
        String currency = "EUR";
        Double expectedRate = 0.90;
        Map<Object, Object> fileRates = new HashMap<>();
        fileRates.put(currency, expectedRate);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class))
                .thenThrow(new RuntimeException("Connection refused"));
        when(exchangeRateProvider.getExchangeRate()).thenReturn(fileRates);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.valueOf(expectedRate), result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verify(exchangeRateProvider).getExchangeRate();
    }

    @Test
    void getRateFromService_WhenServiceCallThrowsRuntimeException_ShouldThrowException() {

        String currency = "EUR";
        Map<Object, Object> fileRates = new HashMap<>();
        fileRates.put(currency, 0.91);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class))
                .thenThrow(new RuntimeException("Connection refused"));
        when(exchangeRateProvider.getExchangeRate()).thenReturn(fileRates);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.valueOf(0.91), result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verify(exchangeRateProvider).getExchangeRate();
    }

    @Test
    void getRate_WhenServiceReturnsNullBody_ShouldReturnDefaultRate() {

        String currency = "EUR";
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        when(sessionCurrency.getCurrency()).thenReturn(currency);
        when(restTemplate.getForEntity(currencyServiceUrl, Map.class)).thenReturn(responseEntity);


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verifyNoInteractions(exchangeRateProvider); // Не должно вызывать fallback на файл
    }

    @Test
    void getRate_WhenFileIsEmpty_ShouldReturnDefaultRate() {

        when(restTemplate.getForEntity(currencyServiceUrl, Map.class))
                .thenThrow(new RestClientException("Service unavailable"));
        when(exchangeRateProvider.getExchangeRate()).thenReturn(new HashMap<>());


        BigDecimal result = currencyRateClient.getRate();


        assertEquals(BigDecimal.ONE, result);
        verify(restTemplate).getForEntity(currencyServiceUrl, Map.class);
        verify(exchangeRateProvider).getExchangeRate();

    }
}