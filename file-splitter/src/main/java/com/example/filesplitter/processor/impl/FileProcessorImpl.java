package com.example.filesplitter.processor.impl;

import com.example.filesplitter.input.FileSplitterInput;
import com.example.filesplitter.processor.FileProcessor;
import com.google.gson.stream.JsonReader;
import org.springframework.stereotype.Service;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public class FileProcessorImpl implements FileProcessor {


    @Override
    public void processFile(FileSplitterInput fileSplitterInput) {
        BufferedReader bufferedReader = null;
        JSONParser jsonParser = new JSONParser();
        try {
            /*bufferedReader = new BufferedReader(new FileReader(fileSplitterInput.getSourcePath()));
            String sCurrentLine;
            while ((sCurrentLine = bufferedReader.readLine()) != null) {
                System.out.println("Current Line is : " + sCurrentLine);
            }*/
            Object obj = jsonParser.parse(new FileReader(fileSplitterInput.getSourcePath()));

            //JSONObject jsonObject = (JSONObject) obj;
            JSONArray jsonArray = (JSONArray) obj;
            int size = jsonArray.size();
            System.out.println("JSON Array Size is : " + size);

            /*Set<String> keys = jsonObject.keySet();

            for(int i=0; i<keys.size(); i++){
                System.out.println("Key is : " + keys.toArray()[i] + " value is : " + jsonObject.get(keys.toArray()[i]));
            }*/

            //System.out.println("Number of Elements in JSON are : " + values.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
