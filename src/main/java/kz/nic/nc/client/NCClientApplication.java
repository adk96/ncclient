package kz.nic.nc.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class NCClientApplication {

    public static void main(String[] args) {        
        SpringApplicationBuilder builder = new SpringApplicationBuilder(NCClientApplication.class);
        builder.headless(false);
        builder.run(args);
    }

}
