package pkg.utils;

import pkg.metadata.Config;
import pkg.metadata.CreateOpenSSH;
import pkg.metadata.MetaDataHandler;
import pkg.metadata.ReadConfig;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static pkg.utils.Constants.FILE_PATH;

public class FileHelper {
    private static final String DELIMITER = "~";

    public boolean createDirectory(String rootFilePath , String directoryName) {
        // check if directory exists
        File directory = new File(rootFilePath + directoryName);
        if (directory.exists()) {
            return false;
        }
        boolean result = false;
        try {
            result = directory.mkdir();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static boolean checkName(String userName,String passWord) throws IOException {
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(FILE_PATH+"user/UserProfile.txt"));

        String username="";
        String password ="";

        while((line = br.readLine()) != null) {
            if (!line.isEmpty()) {
                String[] input = line.split("\\~");
                username = input[0];
                password = input[1];
                if (username.equals(userName) && password.equals(passWord)) {
                    return false;
                }
            }
        }


//        do {
//            if ((line = br.readLine()) == null) {
//                return false;
//            }
//         if((line= br.readLine())!="") {
//             String[] input = line.split("\\~");
//             username = input[0];
//             password = input[1];
//         }
//        } while(!username.equals(userName) && !password.equals(passWord));

        return true;
    }

    public boolean deleteDirectory(String rootFilePath ,String directoryName) {
        File directory = new File(rootFilePath + directoryName);
        return directory.delete();
    }

    public boolean isDirectoryExists(String rootFilePath ,String directoryName) {
        File directory = new File(rootFilePath + directoryName);
        return directory.exists();
    }

    public void writeFileToDirectory(String rootFilePath ,String directoryPath, String content) {
        try {
            FileWriter fileWriter = new FileWriter(rootFilePath + directoryPath + ".txt");
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
            sendDataToOrigin(rootFilePath,directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void appendDataToFile(String rootFilePath ,String directoryPath, String content) {
        try {
            FileWriter fileWriter = new FileWriter(rootFilePath + directoryPath, true);
            fileWriter.write(content);
            fileWriter.close();
            sendDataToOrigin(rootFilePath,directoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String readFileFromDirectory(String rootFilePath ,String directoryPath) {
        StringBuilder content = new StringBuilder();
        try {
            FileReader fileReader = new FileReader(rootFilePath + directoryPath);
            int c;
            while ((c = fileReader.read()) != -1) {
                content.append((char) c);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    // read file from directory and return list of lines using BufferedReader
    public List<String> readFileFromDirectoryToList(String rootFilePath ,String directoryPath) {
        List<String> lines = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(rootFilePath + directoryPath + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public boolean createFile(String rootFilePath ,String fileName) {
        File file = new File(rootFilePath + fileName + ".txt");
        boolean result = false;
        try {
            result = file.createNewFile();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean deleteFile(String rootFilePath ,String fileName) {
        File file = new File(rootFilePath + fileName);
        return file.delete();
    }

    public boolean isFileExists(String rootFilePath ,String fileName) {
        File file = new File(rootFilePath + fileName);
        return file.exists();
    }

    public List<String> readFirstLine(String rootFilePath ,String metadataFilePath) throws IOException {

        BufferedReader metaDataFile = new BufferedReader(new FileReader(rootFilePath + metadataFilePath));
        String headers;
        String[] headerList;
        List<String> resultHeaders = new ArrayList<>();
        while((headers = metaDataFile.readLine())!=null){

            headerList = new String[0];
            headerList = headers.split(DELIMITER);
            if(!headerList[0].equals("PK") && !headerList[0].equals("FK")) {
                resultHeaders.add(headerList[0]);
            }
        }

        return resultHeaders;
    }

    public StringBuilder appendFirstLine(String content, BufferedReader metaDataFile) throws IOException {

        String headers;
        String[] headerList;
        String[] contentList;
        StringBuilder resultOutput = new StringBuilder();

        if (content.contains(",")) {
            contentList = content.split(",");
            for(String h : contentList) {
                resultOutput.append(h);
                resultOutput.append("\t");
            }
        }

        if (content.equals("*")) {
            while ((headers = metaDataFile.readLine()) != null) {

                headerList = headers.split(DELIMITER);

                if (!headerList[0].equals("PK") && !headerList[0].equals("FK")) {
                    resultOutput.append(headerList[0]);
                    resultOutput.append("\t");
                }
            }
        }
        resultOutput.append("\n");
        return resultOutput;
    }



    public int checkNumberOfColumns(String rootFilePath, String filePath) throws IOException {

        return readFirstLine(rootFilePath,filePath).size();
    }

    public String selectedFileContent(String rootFilePath ,String content, String filePath, int index, String query, String metadataFilePath) {


        BufferedReader dataFile;
        StringBuilder resultOutput = new StringBuilder();
        String line;
        String[] splitLine;
        filePath = rootFilePath + filePath;

        try {
            BufferedReader metaDataFile = new BufferedReader(new FileReader(rootFilePath + metadataFilePath));
            List<String> resultHeaders = readFirstLine(rootFilePath,metadataFilePath);
            resultOutput = appendFirstLine(content, metaDataFile);

            dataFile = new BufferedReader(new FileReader(filePath));
            while ((line = dataFile.readLine()) != null) {
                if (query == null & index == -1) {
                    resultOutput = columnClause(resultOutput, line, content, resultHeaders);
                    resultOutput.append("\n");
                }
                else {
                    splitLine = line.split(DELIMITER);
                    if (splitLine[index].equals(query)) {
                        resultOutput = columnClause(resultOutput, line, content, resultHeaders);
                        resultOutput.append("\n");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultOutput.toString();
    }

    public void deleteFromFile(String rootFilePath ,String filePath, int index, String query) {

        String line;
        String[] splitLine;
        int columnIndex = 0;
        List<String> newFile = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(rootFilePath + filePath));
            while ((line = br.readLine()) != null) {

                splitLine = line.split(DELIMITER);
                if (splitLine[index].equals(query)) {

                } else {
                    newFile.add(line);
                }

                FileWriter fw = new FileWriter(rootFilePath + filePath);
                BufferedWriter out = new BufferedWriter(fw);
                for (String newline : newFile) {
                    out.write(newline + "\n");
                }
                out.flush();
                out.close();

            }
            sendDataToOrigin(rootFilePath,filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder columnClause(StringBuilder resultOutput, String line, String content, List<String> resultHeaders) {

        int columnIndex = -1;
        String[] queryColumns = {};
        if (content.contains(",")) {
            queryColumns = content.split(",");
        }

        if (content.equals("*")) {
            resultOutput.append(line.replaceAll(DELIMITER, "\t"));

        } else if (content != "*" || queryColumns.length > 1) {
            List<Integer> columnIndexes = new ArrayList<>();
            for (String h : resultHeaders) {
                columnIndex++;
                for (String column : queryColumns) {
                    if (column.equals(h))
                        columnIndexes.add(columnIndex);
                }
            }
            for (int i : columnIndexes) {
                resultOutput.append(line.split(DELIMITER)[i]);
                resultOutput.append("\t");
            }
        }

        return resultOutput;
    }

    public void sendDataToOrigin(String rootFilePath ,String directoryPath){
        if(rootFilePath.equalsIgnoreCase(Constants.temp)){
            ReadConfig readConfig = new ReadConfig();
            Config config = readConfig.read();
            CreateOpenSSH createOpenSSH = new CreateOpenSSH(config);
            String[] temp = directoryPath.split("/");

            createOpenSSH.directorySend(temp[1],temp[2]);

        }
    }
}