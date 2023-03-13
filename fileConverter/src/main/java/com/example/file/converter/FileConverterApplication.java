package com.example.file.converter;

import com.example.file.converter.validator.HeaderValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.file.converter.service.ConvertXMLToCsvService;

@SpringBootApplication
public class FileConverterApplication implements CommandLineRunner{

    @Autowired
    private HeaderValidator headerValidator;

    public static void main(String[] args) {
        SpringApplication.run(FileConverterApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        ConvertXMLToCsvService convertXMLToCsvService = new ConvertXMLToCsvService();
        convertXMLToCsvService.process(args);
        //headerValidator.readHeaderFile();
        //headerValidator.printHeaderMap();
    }

}