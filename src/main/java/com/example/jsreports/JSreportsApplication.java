package com.example.jsreports;

import com.example.classes.RibData;
import io.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@SpringBootApplication
@RestController
public class JSreportsApplication {

    @PostMapping("/generateReport")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody RibData ribData) {
        try {
            //
            String filePath = ResourceUtils.getFile("classpath:RibTemplate.jrxml")
                    .getAbsolutePath();

            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);

            // Create the report parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("iban", ribData.getIban());
            parameters.put("rib", ribData.getRib());
            parameters.put("fullname", ribData.getFullname());
            parameters.put("currency", ribData.getCurrency());

            // Fill the report with data
            List<RibData> list = new ArrayList<>();
            list.add(ribData);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Export the report to PDF format
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

            // Convert PDF to Base64 string
            byte[] pdfBytes = outputStream.toByteArray();
            String base64String = Base64.getEncoder().encodeToString(pdfBytes);

            Map<String, Object> response = new HashMap<>();
            response.put("data", base64String);

            return ResponseEntity.ok(response);
        } catch (JRException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/GetDetail")
    public  String GetDetail(){
        return "Congratulation";
    }
    @GetMapping("/RibDetails")
    public String RibDetails() {
        return "ggh";
    }


    public static void main(String[] args) {
        SpringApplication.run(JSreportsApplication.class, args);
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.example.jsreports.Controllers")) // Specify the base package of your controllers
                .paths(PathSelectors.any())
                .build().apiInfo(apiInfo());
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("All Reports")
                .description("Jasper Report Vista")
                .version("1.0.0")
                .build();
    }

}
