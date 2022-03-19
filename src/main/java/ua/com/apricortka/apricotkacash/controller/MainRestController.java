package ua.com.apricortka.apricotkacash.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ua.com.apricortka.apricotkacash.enums.TimeInterval;
import ua.com.apricortka.apricotkacash.object.Currency;
import ua.com.apricortka.apricotkacash.object.Rate;
import ua.com.apricortka.apricotkacash.parser.DomParser;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@RestController
public class MainRestController {

    @Value("${my.link.exchange}")
    private String exchangeLink;
    @Value("${my.link.currentUsd}")
    private String currentUsdLink;
    @Value("${my.link.eur}")
    private String eurLink;
    @Value("${my.link.property.date}")
    private String dateProperty;
    @Value("${my.link.format.json}")
    private String jsonFormat;
    @Value("${my.alphavantageapi.btc}")
    private String alphaVantageApiBtc;
    @Value("${my.alphavantageapi.key}")
    private String alphaVantageApiKey;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final Logger log = Logger.getLogger(MainRestController.class);

    @Async
    @GetMapping("")
    public CompletableFuture<String> mainPage() {
        log.info("Слава Україні!");
        return CompletableFuture.completedFuture("Слава Україні!");
    }

    // using RestTemplate
    @Async
    @GetMapping("/usd")
    public CompletableFuture<Rate> getCurrentUsd() {
        log.info("Get USD/UAH from API");
        RestTemplate restTemplate = new RestTemplate();
        Currency[] currencies = restTemplate.getForObject(currentUsdLink, Currency[].class);
        String result = "error";
        try {
            assert currencies != null;
            result = String.valueOf(currencies[0].getRate());
        } catch (Exception e) {
            log.error(e);
        }
        log.info("Return USD/UAH to user");
        return CompletableFuture.completedFuture(new Rate(result));
    }

    // using DOM XML
    @Async
    @GetMapping("/eur/{date}")
    public CompletableFuture<Rate> getEur(@PathVariable String date) {
        log.info("Get EUR/UAH from API by date " + date);
        DomParser domParser = new DomParser();
        String result = "error";
        try {
            result = domParser.parseRate(eurLink + date);
            log.info("Parse is OK");
        } catch (Exception e) {
            log.error(e);
        }
        log.info("Return EUR/UAH to user");
        return CompletableFuture.completedFuture(new Rate(result));
    }

    // using jackson
    @Async
    @GetMapping("/best_rate/{currency}/{interval}")
    public CompletableFuture<Rate> getBestRate(@PathVariable String currency, @PathVariable TimeInterval interval) {
        log.info("Get best rate of " + currency + "/UAH from API in interval " + interval);
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        String result = "error";
        try {
            if (mapper.readValue(new URL(exchangeLink + currency + jsonFormat), Currency[].class).length == 0) {
                throw new IllegalArgumentException();
            }
            switch (interval) {
                case DAY:
                    result = calculateBestRate(currency, 1, mapper);
                    break;
                case WEEK:
                    result = calculateBestRate(currency, 7, mapper);
                    break;
                case MONTH:
                    result = calculateBestRate(currency, 30, mapper);
                    break;
                case YEAR:
                    result = calculateBestRate(currency, 365, mapper);
                    break;
            }
        } catch (Exception e) {
            log.error(e);
            result = "Currency with code " + currency + " not found";
        }
        log.info("Return result to user");
        return CompletableFuture.completedFuture(new Rate(result));
    }

    private String calculateBestRate(String currency, int times, ObjectMapper mapper) throws IOException {
        String tempUrl = exchangeLink + currency + dateProperty;
        LocalDate date = LocalDate.now();
        double result = mapper.readValue(new URL(tempUrl + date.format(dateFormat) + jsonFormat), Currency[].class)[0].getRate();
        double tempResult;
        for (int i = 1; i < times; i++) {
            date = date.minusDays(1);
            tempResult = mapper.readValue(new URL(tempUrl + date.format(dateFormat) + jsonFormat), Currency[].class)[0].getRate();
            if (tempResult > result) {
                result = tempResult;
            }
        }
        return String.valueOf(result);
    }

    // using org.json
    @Async
    @GetMapping("/btc")
    public CompletableFuture<Rate> getCurrentBtc() {
        log.info("Get BTC/UAH from API");
        String result = "error";
        try {
            URL url = new URL(alphaVantageApiBtc + alphaVantageApiKey);
            JSONTokener tokener = new JSONTokener(url.openStream());
            JSONObject root = new JSONObject(tokener);
            result = root.getJSONObject("Realtime Currency Exchange Rate").getString("5. Exchange Rate");
        } catch (Exception e) {
            log.error(e);
        }
        log.info("Return BTC/UAH to user");
        return CompletableFuture.completedFuture(new Rate(result));
    }
}
