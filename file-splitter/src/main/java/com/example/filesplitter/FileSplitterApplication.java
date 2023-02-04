package com.example.filesplitter;

import com.example.filesplitter.input.FileSplitterInput;
import com.example.filesplitter.processor.FileProcessor;
import com.example.filesplitter.processor.impl.FileProcessorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//@SpringBootApplication
public class FileSplitterApplication {

    public static void main(String[] args) {

        try {
            // Enter data using BufferReader
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.print("Enter Source Path: \t");
            String sourcePath = reader.readLine();
            System.out.print("Enter the Line Seperator Count: \t");
            Long lineSeperatorCount = Long.parseLong(reader.readLine());
            System.out.print("Enter Destination Path: \t");
            String destinationPath = reader.readLine();
            FileSplitterInput input = new FileSplitterInput();
            input.setSourcePath(sourcePath);
            input.setLineSeperatorNumber(lineSeperatorCount);
            input.setDestinationPath(destinationPath);
            FileProcessor fileProcessor = new FileProcessorImpl();
            fileProcessor.processFile(input);
            //SpringApplication.run(FileSplitterApplication.class, args);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
