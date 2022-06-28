package pkg.dbstate;

import pkg.utils.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pkg.utils.Constants.DB_FILE_PATH;
import static pkg.utils.Constants.FILE_PATH;

// class that represents the state of the database
// it searches the directory with the name of the database and iterates over all directories to find number of directories
public class DBState {

    // get total number of tables in the database and number of records in each table
    public static String getDBStatus(String dbName) {
        StringBuilder dbStatusString = new StringBuilder();
        Map<String, Integer> tableStatus = new HashMap<>();
        FileHelper fileHelper = new FileHelper();
        int numOfDirs = 0;
        File dbDir = new File(FILE_PATH + DB_FILE_PATH + dbName);
        File[] dirs = dbDir.listFiles();
        if (dirs != null) {
            if (dirs.length == 0) {
                dbStatusString.append("The database is empty");
            } else {
                for (File tableDir : dirs) {
                    if (tableDir.isDirectory()) {
                        numOfDirs++;
                        // find files in each directory and count number of lines
                        // read file with name that does not contain the word "metadata"
                        File[] tableFiles = tableDir.listFiles();
                        if (tableFiles != null) {
                            for (File tableFile : tableFiles) {
                                if (!tableFile.getName().contains("metadata")) {
                                    // read file and count number of lines
                                    String tableName = tableFile.getName().replaceAll(".txt", "");
                                    List<String> fileData = fileHelper.readFileFromDirectoryToList(FILE_PATH,DB_FILE_PATH + dbName + "/" + tableName + "/" + tableName);
//                                    result.add(tableFile.getName() + ": " + fileData.size());
                                    tableStatus.put(tableName, fileData.size());
                                }
                            }
                        }
                    }
                }
                dbStatusString.append("Total Number of tables in the database: ").append(numOfDirs).append(" | ");
                dbStatusString.append("Table Records: ").append(tableStatus);
            }
        } else {
            dbStatusString.append("Error: The database does not exist");
        }
        return dbStatusString.toString();
    }
}
