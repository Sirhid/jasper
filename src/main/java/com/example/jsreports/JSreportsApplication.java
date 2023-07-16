package com.example.jsreports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JSreportsApplication {

    @GetMapping("/GetDetail")
    public  String GetDetail(){
        return "Congratulation";
    }


    public static void main(String[] args) {
        SpringApplication.run(JSreportsApplication.class, args);
    }

    public class ServletInitializerBusinessApplication extends SpringBootServletInitializer {

        @Override
        protected SpringApplicationBuilder configure(
                SpringApplicationBuilder application) {
            return application.sources(JSreportsApplication.class);
        }

    }

}
