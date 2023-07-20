package com.example.Controllers;
import com.example.classes.RibData;
import com.example.jsreports.JSreportsApplication;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
public class RibController {
    private static final Logger logger = LoggerFactory.getLogger(JSreportsApplication.class);
    @Autowired
    private ResourceLoader resourceLoader;
    @PostMapping("/Report")

    public ResponseEntity<Map<String, Object>> Report(@RequestBody RibData ribData) {
        try {

            Resource resource = resourceLoader.getResource("classpath:RibTemplate.jrxml");
            String filePath = resource.getFile().getAbsolutePath();
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

            try{
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

                /// Export the report to PDF format
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


}
