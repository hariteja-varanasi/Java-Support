package com.xml.to.csv.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UtilsClass {

    private static Logger logger = LoggerFactory.getLogger(UtilsClass.class);

    public static void convertExceptionToString(Exception e){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logger.info("Exception is : " + sw.toString());
    }

}
