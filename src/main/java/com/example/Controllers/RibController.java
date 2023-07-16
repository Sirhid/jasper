package com.example.Controllers;
import com.example.classes.RibData;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@RestController
public class RibController {

    @PostMapping("/api/generateReport")
    public ResponseEntity<Map<String, Object>> generateReport(@RequestBody RibData ribData) {
        try {

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

}
