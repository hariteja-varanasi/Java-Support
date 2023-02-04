import java.io.*;
import java.util.logging.Logger;

public class Main {
    public static final Logger logger = Logger.getLogger(String.valueOf(Main.class));

    public static void main(String[] args) {
        long linesWritten = 0;
        int count = 1;
        try {
            BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(System.in));
            System.out.print("Enter Input File Path:");
            String inputFilePath = fileReader.readLine();

            File inputFile = new File(inputFilePath);
           String inputFileExtension =  inputFile.getName().substring(inputFile.getName().lastIndexOf("."),inputFile.getName().length());

            System.out.print("Enter LinesPerSplit:");

            int linesPerSplit = Integer.parseInt(fileReader.readLine());

            InputStream inputFileStream = new BufferedInputStream(new FileInputStream(inputFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputFileStream));

            String line = reader.readLine();

            String fileName = inputFile.getName();

            System.out.print("Enter Output File Path:");
            String outFilePath = fileReader.readLine();

            String outfileName = outFilePath + "/" + fileName;

            while (line != null) {
                File outFile = new File(outfileName + "_" + count +  inputFileExtension);
               Writer writer = new OutputStreamWriter(new FileOutputStream(outFile));

                try{
                    while (line != null && linesWritten < linesPerSplit) {
                        writer.write(line);
                        line = reader.readLine();
                        linesWritten++;
                    }
                    writer.close();
                 new Main().encryptFile(outFile,outFilePath,outFile.getName());


                    logger.info(outFile.toString());
                    linesWritten = 0;//next file
                    count++;//next file count
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            logger.info("Total file split count:" + count);
            reader.close();


        } catch (Exception e) {
            logger.info("error");
            e.printStackTrace();
        }

    }

    private void encryptFile(File inputFile, String outputFilepath,String fileName) {
        String cmd = "gpg -e -r pgp-bu " + inputFile.getPath();
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            inputStream = process.getInputStream();
            byte[] dataArray =  inputStream.readAllBytes();
            fileOutputStream = new FileOutputStream(outputFilepath + "\\"  + fileName);
            fileOutputStream.write(dataArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(inputStream != null){
                try {
                    inputStream.close();
                }
                catch (Exception e2){
                    e2.printStackTrace();
                }
            }
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                }
                catch (Exception e1){
                   e1.printStackTrace();
                }
            }


        }
    }
}