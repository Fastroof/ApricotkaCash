package ua.com.apricortka.apricotkacash;

import org.apache.log4j.Logger;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer {

    private static final Logger log = Logger.getLogger(ServletInitializer.class);

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        log.info("Initialize servlet (configure)");
        return application.sources(ApricotkaCashApplication.class);
    }

}
