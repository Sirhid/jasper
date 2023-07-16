package com.example.jsreports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class JSreportsApplication {

    @GetMapping("/GetDetail")
    public  String GetDetails(){
        return "Congratulation";
    }


    public static void main(String[] args) {
        SpringApplication.run(JSreportsApplication.class, args);
    }

}
