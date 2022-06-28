package pkg.metadata;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MetaDataHandler {
    private final static String GLOBAL_FILE = "Global_METADATA";
    private final static String LOCAL_FILE = "Local_METADATA";
    private static final String DELIMITER = "~";
    private Config config;

    public MetaDataHandler(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public List<String> metadataReader(String filePath, String fileName){
        List<String> metadataList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(filePath + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if(fileName.contains("Global")){
                    if(!line.contains(config.getVmName())){
                        metadataList.add(line);
                    }
                }else if(fileName.contains("Local")){
                    if(line.contains(config.getVmName())){
                        metadataList.add(line);
                    }
                }
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return metadataList;
    }

    public void MetadataWriter(String content,String filePath, String fileName, boolean append){
        try {
            FileWriter fileWriter = new FileWriter(filePath + fileName, append);
            fileWriter.write(content);
            fileWriter.write("\n");
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void clearFile(String filepath, String fileName){
        try {
            FileWriter fileWriter = new FileWriter(filepath + fileName);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void deleteFile(String filepath, String fileName){
        File myObj = new File(filepath+fileName);
    }

    public String checkTable(String table){
        String vmName = "";
        String tempFilePath = config.getSourceGetFilePath();
        String localMetaDataFilePath = config.getSourcePutFilePath();
        List<String> globalTable = metadataReader(tempFilePath,GLOBAL_FILE);
        List<String> localTable = metadataReader(localMetaDataFilePath,LOCAL_FILE);

        for(String s:localTable){
            String[] tableParser = s.split(DELIMITER);
            String tableName = tableParser[2];
            if(tableName.equalsIgnoreCase(table)){
                vmName = tableParser[0];
                return vmName;
            }
        }
        for(String s:globalTable){
            String[] tableParser = s.split(DELIMITER);
            String tableName = tableParser[2];
            if(tableName.equalsIgnoreCase(table)){
                vmName = tableParser[0];
                return vmName;
            }
        }
        return vmName;
    }

    public Database checkDatabase(String database){
        Database databaseStructure = new Database();
        String vmName = "";
        List<String> tables = new ArrayList<>();
        String tempFilePath = config.getSourceGetFilePath();
        String localMetaDataFilePath = config.getSourcePutFilePath();
        List<String> globalTable = metadataReader(tempFilePath,GLOBAL_FILE);
        List<String> localTable = metadataReader(localMetaDataFilePath,LOCAL_FILE);

        for(String s:localTable){
            String[] tableParser = s.split(DELIMITER);
            String databaseName = tableParser[1];
            if(databaseName.equalsIgnoreCase(database)){
                vmName = tableParser[0];
                if(tableParser.length>2){
                    tables.add(tableParser[2]);
                }
            }
        }
        for(String s:globalTable){
            String[] tableParser = s.split(DELIMITER);
            String databaseName = tableParser[1];
            if(databaseName.equalsIgnoreCase(database)){
                vmName = tableParser[0];
                if(tableParser.length>2){
                    tables.add(tableParser[2]);
                }
            }
        }
        databaseStructure.setVmName(vmName);
        databaseStructure.setTables(tables);
        return databaseStructure;
    }

    public void addMetaData(String database,String table,boolean status){
        String message;
        if(status){
            message = config.getVmName()+DELIMITER+database+DELIMITER+table+DELIMITER+"lock";
        }else{
            message = config.getVmName()+DELIMITER+database+DELIMITER+table;
        }
        MetadataWriter(message,config.getSourcePutFilePath(),LOCAL_FILE,true);
        fetchGlobalMetaData();
    }

    public void fetchGlobalMetaData(){
        getGlobalMetaData();
        mergeGlobalMetaData();
        sendGlobalMetaData();
    }

    public void getGlobalMetaData(){
        CreateOpenSSH createOpenSSH = new CreateOpenSSH(config);
        createOpenSSH.fileTransfer("receive",GLOBAL_FILE);
    }

    public void sendGlobalMetaData(){
        CreateOpenSSH createOpenSSH = new CreateOpenSSH(config);
        createOpenSSH.fileTransfer("send",GLOBAL_FILE);
    }

    public void mergeGlobalMetaData(){
        String tempFilePath = config.getSourceGetFilePath();
        String localMetaDataFilePath = config.getSourcePutFilePath();
        List<String> tempGlobalMetaDataList = metadataReader(tempFilePath,GLOBAL_FILE);
        List<String> localMetaDataList = metadataReader(localMetaDataFilePath,LOCAL_FILE);

        clearFile(config.getSourcePutFilePath(),GLOBAL_FILE);
        deleteFile(config.getSourceGetFilePath(),GLOBAL_FILE);
        for(String write:localMetaDataList){
            MetadataWriter(write,localMetaDataFilePath,GLOBAL_FILE,true);
        }
        for(String write:tempGlobalMetaDataList){
            MetadataWriter(write,localMetaDataFilePath,GLOBAL_FILE,true);
        }

    }

    public void getTables(String databaseName,Database database){
        CreateOpenSSH createOpenSSH = new CreateOpenSSH(config);
        createOpenSSH.directoryTransfer("receive",databaseName,database.getTables());
    }

    public void updateTables(String databaseName,String tableName){
        ReadConfig readConfig = new ReadConfig();
        Config config = readConfig.read();
        CreateOpenSSH createOpenSSH = new CreateOpenSSH(config);
        createOpenSSH.directorySend(databaseName,tableName);
    }
}
