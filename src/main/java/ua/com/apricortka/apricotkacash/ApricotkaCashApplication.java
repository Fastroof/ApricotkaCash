package ua.com.apricortka.apricotkacash;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class ApricotkaCashApplication {

    private static final Logger log = Logger.getLogger(ApricotkaCashApplication.class);

    public static void main(String[] args) {
        log.info("Run Apricotka Cash Application");
        SpringApplication.run(ApricotkaCashApplication.class, args);
    }

}
