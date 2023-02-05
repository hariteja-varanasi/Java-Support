package com.xml.to.csv;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.xml.to.csv.utils.UtilsClass;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@SpringBootApplication
public class XmlToCsvApplication {

    private static Logger logger = LoggerFactory.getLogger(XmlToCsvApplication.class);

    private static List<String> offenderIDs = new ArrayList<String>();

    private static Map<String, List<Map<String, String>>> offenderIdDetailsMap = new LinkedHashMap<String, List<Map<String, String>>>();

    private static String sourceFilePath;

    private static String destinationFilePath;

    private static String csvFilePath;

    @Value("${source.file.path}")
    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    @Value("${destination.file.path}")
    public void setDestinationFilePath(String destinationFilePath) {
        this.destinationFilePath = destinationFilePath;
    }

    @Value("${csv.file.path}")
    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    public static void main(String[] args) {
        SpringApplication.run(XmlToCsvApplication.class, args);
        List<File> unzippedFiles = unzipFiles(sourceFilePath, destinationFilePath);
        if (unzippedFiles != null && unzippedFiles.size() > 0) {
            loadOffenderIDs(unzippedFiles);
            loadOffernderIdDetailsMap(unzippedFiles);
            writeToCSV(offenderIdDetailsMap);
        }
        /*offenderIdDetailsMap.entrySet().stream().forEach((entry) -> {
            logger.info("******************************************************************* OFFENDER ID : " + entry.getKey() + " *******************************************************************");
            entry.getValue().stream().forEach((map)->{
                map.entrySet().stream().forEach((mapEntry) -> {
                    logger.info("Key is : " + mapEntry.getKey() + " Value is : " + mapEntry.getValue());
                });
            });
            System.out.println();
        });*/
    }

    private static void writeToCSV(Map<String, List<Map<String, String>>> offenderIdDetailsMap){
        try {
            CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath));
            //FileWriter csvWriter = new FileWriter("D:\\Java\\temp\\files\\output\\output.csv");
            List<String> csvStringList = new ArrayList<String>();
            List<String[]> csvStringArrayList = new ArrayList<String[]>();
            for(String offenderId : offenderIdDetailsMap.keySet()){
                String csvString = "{ \"" + offenderId + "\" : { ";
             for(Map<String, String> values : offenderIdDetailsMap.get(offenderId)){
                 int i=0;
                 for(String key : values.keySet()) {
                     i++;
                     if(i != values.size()) {
                         csvString = csvString + "\"" + key + "\"" + ":" + "\"" + values.get(key) + "\"" + ",";
                     }
                     else {
                         csvString = csvString + "\"" + key + "\"" + ":" + "\"" + values.get(key) + "\"";
                     }
                     if(i == values.size()) {
                         csvString = csvString + "} }";
                     }
                 }
             }
             //logger.info("CSV String is : " + csvString);
             csvStringList.add(csvString);
            }
            String[] csvStringArray = csvStringList.toArray(new String[0]);
            csvStringArrayList.add(csvStringArray);
            csvWriter.writeAll(csvStringArrayList);
            csvWriter.close();
            logger.info("CSV File Created.");

            try {
                FileReader filereader = new FileReader(csvFilePath);
                CSVReader csvReader = new CSVReader(filereader);
                String[] nextRecord;
                while ((nextRecord = csvReader.readNext()) != null) {
                    for (String cell : nextRecord) {
                        offenderIDs.stream().forEach((id) -> {
                            if(cell.contains("\"" + id + "\"")) {
                                try {
                                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(cell);
                                    if(jsonObject != null) {
                                        logger.info("Complete JSON String is : " + jsonObject.toString());
                                    }
                                    JSONObject offenderIdJSON = (JSONObject) jsonObject.get(id);
                                    if(offenderIdJSON != null) {
                                        String offenderIdJSONString = offenderIdJSON.toString();
                                        logger.info("JSON String is : " + offenderIdJSONString);
                                    }
                                }
                                catch (Exception e) {
                                    UtilsClass.convertExceptionToString(e);
                                }
                            }
                        });
                    }
                    //System.out.println();
                }
                csvReader.close();
                filereader.close();
            }
            catch (Exception e) {
                UtilsClass.convertExceptionToString(e);
            }
        }
        catch (Exception e) {
            UtilsClass.convertExceptionToString(e);
        }
    }

    private static void loadOffernderIdDetailsMap(List<File> unzippedFiles){
        for(String offenderId : offenderIDs) {
            List<Map<String, String>> offenderIdDetailsList = new ArrayList<Map<String, String>>();
            offenderIdDetailsList.add(loadOffenderDetails(unzippedFiles, offenderId));
            offenderIdDetailsMap.put(offenderId, offenderIdDetailsList);
        }
    }

    private static Map<String, String> loadOffenderDetails(List<File> unzippedFiles, String offenderId) {
        Map<String, String> offenderDetailsMap = new LinkedHashMap<String, String>();
        for(File file : unzippedFiles) {
            getInmateDetailsForFile(file, offenderId, offenderDetailsMap);
        }
        return offenderDetailsMap;
    }

    private static void getInmateDetailsForFile(File file, String offenderId, Map<String, String> offenderIdDetailsMap) {
        NodeList nodeList = getInmateDetailsForFile(file, offenderId);
        for(int i=0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if(!element.getNodeName().equals("OFFENDER_ID")) {
                    offenderIdDetailsMap.put(element.getNodeName() + file.getName().substring(file.getName().indexOf("."), file.getName().lastIndexOf(".")),
                            (element.getTextContent() != null && (!element.getTextContent().isEmpty() || !element.getTextContent().isBlank() || element.getTextContent().length() > 0)) ? element.getTextContent() : "NA");
                }
            }
        }
    }

    private static NodeList getInmateDetailsForFile(File file, String offenderId) {
        NodeList inmateNodeList = null;
        try {
            Document document = getDocumentForFile(file);
            if(document != null) {
                inmateNodeList = document.getElementsByTagName("INMATE");
                for (int i = 0; i < inmateNodeList.getLength(); i++) {
                    Node inmateNode = inmateNodeList.item(i);
                    NodeList inmateChildNodes = inmateNode.getChildNodes();
                    for (int j = 0; j < inmateChildNodes.getLength(); j++) {
                        Node inmateChildNode = inmateChildNodes.item(j);
                        if (inmateChildNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element inmateChildElement = (Element) inmateChildNode;
                            if(inmateChildElement.getNodeName().equals("OFFENDER_ID") && inmateChildElement.getTextContent().equals(offenderId)){
                                return inmateChildNodes;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            UtilsClass.convertExceptionToString(e);
        }
        return inmateNodeList;
    }

    private static void loadOffenderIDs(List<File> unzippedFiles){
        for (File file : unzippedFiles) {
            try {
                Document document = getDocumentForFile(file);
                if(document != null) {
                    NodeList inmateNodeList = document.getElementsByTagName("INMATE");
                    for (int i = 0; i < inmateNodeList.getLength(); i++) {
                        Node inmateNode = inmateNodeList.item(i);
                        NodeList inmateChildNodes = inmateNode.getChildNodes();
                        for (int j = 0; j < inmateChildNodes.getLength(); j++) {
                            Node inmateChildNode = inmateChildNodes.item(j);
                            if (inmateChildNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element inmateChildElement = (Element) inmateChildNode;
                                if(inmateChildElement.getNodeName().equals("OFFENDER_ID") && !offenderIDs.contains(inmateChildElement.getTextContent())){
                                    offenderIDs.add(inmateChildElement.getTextContent());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                UtilsClass.convertExceptionToString(e);
            }
        }

    }

    private static Document getDocumentForFile(File file) {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();
            return document;
        }
        catch (Exception e) {
            UtilsClass.convertExceptionToString(e);
            return null;
        }
    }

    private static List<File> unzipFiles(String sourceFilePath, String destinationPath) {
        File destDir = new File(destinationPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        FileInputStream fileInputStream;
        List<File> unzippedFiles = new ArrayList<File>();
        byte[] buffer = new byte[1024];
        try {
            fileInputStream = new FileInputStream(sourceFilePath);
            ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                File newFile = new File(destDir + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                unzippedFiles.add(newFile);
                fileOutputStream.close();
                zipInputStream.closeEntry();
                zipEntry = zipInputStream.getNextEntry();
            }
            zipInputStream.closeEntry();
            zipInputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            UtilsClass.convertExceptionToString(e);
        }
        return unzippedFiles;
    }

}
