package com.example.file.converter.validator;

import com.example.file.converter.service.UserDefineNode;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HeaderValidator {

    private Map<Integer, String> headerIndexMap = readHeaderFile();

    private Map<Integer, UserDefineNode> userDefineNodeMap = new HashMap<Integer, UserDefineNode>();

    public Map<Integer, String> readHeaderFile()
    {
        BufferedReader bufferedReader;
        Map<Integer, String> headerIndexMap = null;
        try {
            headerIndexMap = new HashMap<Integer, String>();
            InputStream is = HeaderValidator.class.getResourceAsStream("/headers.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int index = 0;
            String header = bufferedReader.readLine();

            while(header != null) {
                headerIndexMap.put(index, header);
                header = bufferedReader.readLine();
                index++;
            }
            bufferedReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return headerIndexMap;
    }

    private void addMissingHeaders(Map<String,List<List<UserDefineNode>>> outputMap) {
        for(Map.Entry entry : outputMap.entrySet()) {
            List<List<UserDefineNode>> nodeList = (List<List<UserDefineNode>>) entry.getValue();
            for(int i=0; i< nodeList.size(); i++) {
                List<UserDefineNode> nodeListInner = nodeList.get(i);
                List<String> existingHeaders = new ArrayList<String>();
                for(UserDefineNode node: nodeListInner) {
                    existingHeaders.add(node.getHeader());
                }
                for(String standardHeader : headerIndexMap.values()){
                    if(!existingHeaders.contains(standardHeader)){
                        UserDefineNode userDefineNode = new UserDefineNode();
                        userDefineNode.setHeader(standardHeader);
                        userDefineNode.setValue("");
                        nodeListInner.add(userDefineNode);
                    }
                }
            }
            /*for (List<UserDefineNode> nodeListInner : nodeList) {
                List<String> existingHeaders = new ArrayList<String>();
                for(UserDefineNode node: nodeListInner) {
                    existingHeaders.add(node.getHeader());
                }
                for(String standardHeader : headerIndexMap.values()){
                    if(!existingHeaders.contains(standardHeader)){
                        UserDefineNode userDefineNode = new UserDefineNode();
                        userDefineNode.setHeader(standardHeader);
                        userDefineNode.setValue("");
                        nodeListInner.add(userDefineNode);
                    }
                }
            }*/
        }
    }

    private void sortUserDefinedNodes(Map<String,List<List<UserDefineNode>>> outputMap) {
        for (Map.Entry entry : outputMap.entrySet()) {
            List<List<UserDefineNode>> originalNodeList = (List<List<UserDefineNode>>) entry.getValue();
            for(int i=0; i< originalNodeList.size(); i++) {
                List<UserDefineNode> modifiedNodeList = new ArrayList<UserDefineNode>();
                List<UserDefineNode> nodeListInner = originalNodeList.get(i);
                for (Map.Entry headerIndexEntry : headerIndexMap.entrySet()) {
                    for (UserDefineNode node : nodeListInner) {
                        if (node.getHeader().equals(String.valueOf(headerIndexEntry.getValue()))) {
                            modifiedNodeList.add(Integer.parseInt(String.valueOf(headerIndexEntry.getKey())), node);
                        }
                    }
                }
                originalNodeList.set(i, modifiedNodeList);
            }

            /*for (Map.Entry headerIndexEntry : headerIndexMap.entrySet()) {
                for (List<UserDefineNode> nodeListInner : originalNodeList) {
                    for (UserDefineNode node : nodeListInner) {
                        if (node.getHeader().equals(headerIndexEntry.getValue())) {
                            modifiedNodeList.add(Integer.parseInt(String.valueOf(headerIndexEntry.getKey())), node);
                        }
                    }
                }
            }
            originalNodeList.clear();
            originalNodeList.add(0, modifiedNodeList);*/
        }
    }

    public void validateUserDefinedNodeMap(Map<String,List<List<UserDefineNode>>> outputMap) {
        addMissingHeaders(outputMap);
        sortUserDefinedNodes(outputMap);
    }

    public void printHeaderMap() {
        for(Integer index : headerIndexMap.keySet()) {
            System.out.println("Index is : " + index + " Header is : " + headerIndexMap.get(index));
        }
    }

    private Integer getMapHeaderIndex(String header) {
        Integer headerIndex = -1;
        for(Map.Entry entry : headerIndexMap.entrySet()) {
            if(String.valueOf(entry.getValue()).equals(header)) {
                headerIndex = Integer.parseInt(String.valueOf(entry.getKey()));
                break;
            }
        }
        return headerIndex;
    }

    private String getMapHeaderByIndex(Integer headerIndex) {
        String header = new String();
        return String.valueOf(headerIndexMap.get(header));
    }

}
