package com.example.jsreports;
import com.example.classes.RibData;
import com.example.classes.customer;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
@SpringBootApplication
@RestController
public class JSreportsApplication {
    private static final Logger logger = LoggerFactory.getLogger(JSreportsApplication.class);
    String VistaLogoBase64="";

    @Autowired
    private ResourceLoader resourceLoader;



    @PostMapping("/generateReport")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody RibData ribData) {

        try {
            logger.error("Saheed 124");

            Resource img = resourceLoader.getResource("classpath:vistalogobase64.txt");
            InputStream inputStream = img.getInputStream();
            logger.error("Saheed 124" + inputStream);

            // Create a BufferedReader to read the file content
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            logger.error("Saheed 124" + reader);

            String line;
            while ((line = reader.readLine()) != null) {
                VistaLogoBase64= line;
                logger.error("Saheed 124" + line);

            }
            reader.close();
            Resource resource = resourceLoader.getResource("classpath:RibTemplate.jrxml");
            String filePath = resource.getFile().getAbsolutePath();
            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            // Create the report parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("iban", ribData.getIban());
            parameters.put("rib", ribData.getRib());
            parameters.put("fullname", ribData.getFullname());
            parameters.put("currency", ribData.getCurrency());
            parameters.put("VistaLogoBase64", VistaLogoBase64);

            logger.error("Saheed 76" + VistaLogoBase64);

            // Fill the report with data
            List<RibData> list = new ArrayList<>();
            list.add(ribData);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

            try{
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

                // Export the report to PDF format
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
                logger.error("Saheed is here "+ outputStream);

                // Convert PDF to Base64 string
                byte[] pdfBytes = outputStream.toByteArray();
                String base64String = Base64.getEncoder().encodeToString(pdfBytes);

                Map<String, Object> response = new HashMap<>();
                response.put("data", base64String);
                logger.error("Saheed is here "+ response);
                return ResponseEntity.ok(response);

            }catch (Exception ex){
                logger.error("saheed here 107" + ex.getMessage());

            }

        } catch (JRException ex) {
            logger.error(ex.getMessage());
            ex.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return null;
    }




    @GetMapping("/GetCustomer")
    public  ResponseEntity <Map<String, Object>>  GetCustomer(@RequestBody customer payload){
        try{

            logger.error("Saheed 124");

            Resource img = resourceLoader.getResource("classpath:vistalogobase64.txt");
            InputStream inputStream = img.getInputStream();
            logger.error("Saheed 124" + inputStream);

            // Create a BufferedReader to read the file content
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                VistaLogoBase64= line;
            }
            reader.close();


            Resource resource = resourceLoader.getResource("classpath:customerreport.jrxml");
            String filePath = resource.getFile().getAbsolutePath();

            JasperReport jasperReport = JasperCompileManager.compileReport(filePath);
            // Create the report parameters
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("customername", payload.getcustomername());
            parameters.put("phonenumber", payload.getphonenumber());
            parameters.put("userid", payload.getuserid());
            parameters.put("dob", payload.getdob());
            parameters.put("VistaLogoBase64", VistaLogoBase64);


            // Fill the report with data
            List<customer> list = new ArrayList<>();
            list.add(payload);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

            try{
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

            }catch (Exception ex){
                logger.error("saheed here 181" + ex.getMessage());
                return  null;

            }
        }catch (Exception ex){
            logger.error("saheed here 186" + ex.getMessage());

        }
        return  null;

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
