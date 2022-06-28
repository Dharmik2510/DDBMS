package pkg.queryengine;

import pkg.dbstate.DBState;
import pkg.logger.GlobalLogger;
import pkg.metadata.Database;
import pkg.metadata.MetaDataHandler;
import pkg.transaction.TransactionManager;
import pkg.usersession.UserSession;
import pkg.utils.Constants;
import pkg.utils.FileHelper;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pkg.utils.Constants.*;

public class QueryEngine {
    UserSession userSession;
    FileHelper fileHelper;
    GlobalLogger globalLogger;
    TransactionManager transactionManager;
    MetaDataHandler metaDataHandler;
    Logger log = Logger.getLogger(QueryEngine.class.getName());

    public QueryEngine(UserSession userSession, FileHelper fileHelper, GlobalLogger globalLogger, TransactionManager transactionManager,MetaDataHandler metaDataHandler) {
        this.userSession = userSession;
        this.fileHelper = fileHelper;
        this.globalLogger = globalLogger;
        this.transactionManager = transactionManager;
        this.metaDataHandler = metaDataHandler;
    }

    public void executeQuery(Scanner scanner) throws IOException {
        List<String> transactionQueries = transactionManager.getTransactionQueries();
        String query;
        while (true) {
            System.out.println("Please enter a query (enter 'quit' to exit):");
            query = scanner.nextLine();
            if (query.equalsIgnoreCase(QUIT)) {
                System.out.println("Exiting...");
                break;
            }
            if(query.trim().isEmpty()) {
                System.out.println("Invalid query");
                continue;
            }
            System.out.println("Executing query: " + query);
            if(query.equalsIgnoreCase("START TRANSACTION") && transactionManager.isTransaction()==false){
                String[] queryParts = query.split(" ");
                if(isNoDatabaseSelected(queryParts)) {
                    System.out.println("Error in query: No database selected. Please select a database first using 'use' command");
                    continue;
                }
                System.out.println("Transaction has started.");
                globalLogger.addLog(GENERAL_LOG, "Transaction has started in  '"+userSession.getSelectedDatabase()+"'");
                globalLogger.addLog(EVENT_LOG, "@" + userSession.getUserName() + " has started transaction on " + userSession.getSelectedDatabase());

                transactionManager.setTransaction(true);
                continue;
            }else if(query.equalsIgnoreCase("START TRANSACTION") && transactionManager.isTransaction()==true){
                System.out.println("Transaction is already started. Please commit or rollback.");

                continue;
            }else if(query.equalsIgnoreCase("COMMIT") && transactionManager.isTransaction()==false){
                System.out.println("Transaction is not yet started. Please start a transaction");
                continue;
            }else if(query.equalsIgnoreCase("COMMIT") && transactionManager.isTransaction()==true){
                transactionManager.setTransaction(false);
                for(String transactionQuery:transactionQueries){
                    String[] queryParts = transactionQuery.split(" ");
                    run(transactionQuery,queryParts);
                }
                System.out.println("Commit has done.");
                globalLogger.addLog(GENERAL_LOG, "Transaction has been committed in  '"+ userSession.getSelectedDatabase()+"'");
                globalLogger.addLog(EVENT_LOG, "@" + userSession.getUserName() + " has completed transaction on " + userSession.getSelectedDatabase());
                continue;
            }else if(query.equalsIgnoreCase("ROLLBACK") && transactionManager.isTransaction()==false){
                System.out.println("Transaction is not yet started. Please start a transaction");
            }else if(query.equalsIgnoreCase("ROLLBACK") && transactionManager.isTransaction()==true){
                transactionManager.setTransaction(false);
                System.out.println("ROLLBACK has done.");
                continue;
            }
            if(transactionManager.isTransaction()){
                transactionQueries.add(query);
                transactionManager.setTransactionQueries(transactionQueries);
                continue;
            }
            String[] queryParts = query.split(" ");


            if(isNoDatabaseSelected(queryParts)) {
                System.out.println("Error in query: No database selected. Please select a database first using 'use' command");
                continue;
            }
            run(query,queryParts);
        }
    }

    public void run(String query,String[] queryParts) throws IOException {

        String queryType = queryParts[0].toLowerCase(Locale.ROOT);
        switch (queryType) {
            case USE:
                useDatabase(query, queryParts);
                break;
            case INSERT:
                executeInsert(query, queryParts);
                break;
            case SELECT:
                executeSelect(query, queryParts);
                break;
            case DELETE:
                executeDelete(query, queryParts);
                break;
            case UPDATE:
            case CREATE:
                // create StringTokenizer to split query into parts
                StringTokenizer st = new StringTokenizer(query, " ");
                if (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    if (token.equalsIgnoreCase(UPDATE)) {
                        executeUpdate(st, query);
                    } else if (token.equalsIgnoreCase(CREATE)) {
                        // convert CREATE queries to lowercase
                        st = new StringTokenizer(query.toLowerCase(Locale.ROOT), " ");
                        // get the next token
                        st.nextToken();
                        if (st.hasMoreTokens()) {
                            executeCreate(st, query);
                        }
                    }
                } else {
                    System.out.println("Error in query: Query type is not specified");
                }
                break;
            default:
                System.out.println("Invalid query");
        }
    }

    public boolean isNoDatabaseSelected(String[] queryParts) {
        return queryParts.length>1 && (!queryParts[0].equalsIgnoreCase(USE) && !queryParts[1].equalsIgnoreCase(DATABASE)) && userSession.getSelectedDatabase() == null;
    }

    public void useDatabase(String query, String[] queryParts) {
        Constants.setFilePath(original);
        if (queryParts.length < 2) {
            System.out.println("Error in query: Database name is not specified");
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + query + INVALID_QUERY);
            return;
        }
        String databaseName = queryParts[1].toLowerCase(Locale.ROOT);
        String vm = "";
        if(!fileHelper.isDirectoryExists(FILE_PATH,DB_FILE_PATH + databaseName)) {
            metaDataHandler.fetchGlobalMetaData();
            Database databaseStructure = metaDataHandler.checkDatabase(databaseName);
            vm = databaseStructure.getVmName();
            if(!vm.equalsIgnoreCase(userSession.getVirtualMachineName()) && vm.length()>0){
                System.out.println("Fetching database: "+databaseName+" from "+vm);
                globalLogger.addLog(GENERAL_LOG, "Database '"+databaseName+"' selected successfully on " + vm);
                globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + databaseName + " " + vm  + " " +  query + VALID_QUERY);
                userSession.setSelectedDatabase(databaseName);
                userSession.setVirtualMachineName(vm);
                metaDataHandler.getTables(databaseName,databaseStructure);
                Constants.setFilePath(temp);
            }else {
                System.out.println("Error in query: Database " + databaseName + " does not exist");
                globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName()+ " " + query + INVALID_QUERY);
            }
            return;
        }
        userSession.setSelectedDatabase(databaseName);
        System.out.println("Database selected successfully");
        userSession.setVirtualMachineName(metaDataHandler.getConfig().getVmName());
        Constants.setFilePath(original);
        globalLogger.addLog(GENERAL_LOG, "Database '"+databaseName+"' selected successfully on " + userSession.getVirtualMachineName());
        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + VALID_QUERY);
    }

    public void executeCreate(StringTokenizer st, String query) {
        if(st.hasMoreTokens()) {
            String createType = st.nextToken();
            switch (createType) {
                case TABLE:
                    createTable(st, query);
                    break;
                case DATABASE:
                    createDatabase(st, query);
                    break;
                default:
                    System.out.println("Invalid query");
            }
        } else {
            System.out.println("Error in query: Create entity type is not specified");
        }
    }

    // INSERT INTO STUDENT VALUES (1,"TEXT",25.0)
    public void executeInsert(String query, String[] queryParts) throws IOException {

        int len = queryParts.length;
        String errorMessage = "";
        long startTime = System.currentTimeMillis();

        if (queryParts[1].toLowerCase(Locale.ROOT).equals(INTO)) {
            String tableName = queryParts[2].toLowerCase(Locale.ROOT);
            String tablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName + "/" + tableName + ".txt";
            String metadataTablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName + "/" + tableName + "_metadata.txt";
            if (fileHelper.isDirectoryExists(FILE_PATH,tablePath)) //If table exists
            {
                String values = queryParts[3].toLowerCase(Locale.ROOT);
                if (values.equals(VALUES)) {
                    int tableColumns = fileHelper.checkNumberOfColumns(FILE_PATH,metadataTablePath);
                    int queryColumns = len - VALUES_INDEX;
                    if (tableColumns == queryColumns) {

                        String record = "";
                        for (int i = 4; i < len; i++) {
                            record = record + queryParts[i].toLowerCase(Locale.ROOT).replaceAll("[,\"]", "")
                                    .replaceAll("[\",]", "");
                            if (i != (len-1)) {
                                record = record + DELIMITER;
                            }
                        }
                        String openingBraces = String.valueOf(record.charAt(0));
                        String closingBraces = String.valueOf(record.charAt(record.length()-1));
                        String cleanRecord = record.replace(openingBraces, "")
                                .replace(closingBraces, "") + NEW_LINE;
                        fileHelper.appendDataToFile(FILE_PATH,tablePath, cleanRecord);
                        log.info("(1) row affected.");
                        // print success message
                        System.out.println("Row inserted in '" + tableName + "' successfully");
                        // get end time
                        long endTime = System.currentTimeMillis();
                        // calculate time taken
                        long timeTaken = endTime - startTime;
                        globalLogger.addLog(GENERAL_LOG, "Row inserted in  '" + tableName + "' successfully in " + timeTaken + " ms on " + userSession.getVirtualMachineName());
                        globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(userSession.getSelectedDatabase()));
                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " +   query + VALID_QUERY);
                        return;
                    } else {
                        errorMessage = "The input values do not match with table definition.";
                    }
                } else {
                    errorMessage = "There is an error in the query";
                }
            } else {
                errorMessage = "The table does not exists";
            }
        } else {
            errorMessage = "There is an error in the query";
        } if(errorMessage.length()>0) {
            log.info(errorMessage);
            System.out.println(errorMessage);
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + userSession.getVirtualMachineName() +  INVALID_QUERY);
            return;
        }
    }

    //SELECT * FROM STUDENTS WHERE name= "sdfs";
    public void executeSelect(String query, String[] queryParts) throws IOException {

        int len = queryParts.length, index = -1;
        String queryClause = null;
        String content = queryParts[1].toLowerCase(Locale.ROOT);
        String errorMessage = "";
        long startTime = System.currentTimeMillis();

        if (content.matches("[*,0-9a-zA-Z$_]+")) {

            String fromKeyword = queryParts[2].toLowerCase(Locale.ROOT);
            if (fromKeyword.equals(FROM)) {

                String tableName = queryParts[3].toLowerCase(Locale.ROOT);
                String tablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName + "/" + tableName + ".txt";
                String metadataTablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName + "/" + tableName + "_metadata.txt";
                //Query Logs
                if (fileHelper.isDirectoryExists(FILE_PATH,tablePath)) //If table exists
                {
                    if (len == 4) {
                        System.out.println(fileHelper.selectedFileContent(FILE_PATH,content, tablePath, index, queryClause, metadataTablePath));
                        // print success message
                        System.out.println("Data retrieved successfully");
                        // get end time
                        long endTime = System.currentTimeMillis();
                        // calculate time taken
                        long timeTaken = endTime - startTime;
                        globalLogger.addLog(GENERAL_LOG, "Data retrieved from '" + tableName + "' successfully in " + timeTaken + " ms on " + userSession.getVirtualMachineName());
                        globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(userSession.getSelectedDatabase()));
                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + VALID_QUERY);
                    }
                    if (len > 4) {
//                        index = 0;
                        String whereCondition = queryParts[4].toLowerCase(Locale.ROOT);
                        if (whereCondition.equals(WHERE)){
                            List<String> headers = fileHelper.readFirstLine(FILE_PATH,metadataTablePath);
                            String whereClause = queryParts[5].toLowerCase(Locale.ROOT).replaceAll("[^0-9a-zA-Z$_]", "");

                            for (String header : headers) {
                                index++;
                                if (header.equals(whereClause)) {
                                    if (queryParts[6].equals("=")) {
                                        queryClause = queryParts[7].toLowerCase(Locale.ROOT).replaceAll("[^0-9a-zA-Z$_]", "");
                                        System.out.println(fileHelper.selectedFileContent(FILE_PATH,content, tablePath, index, queryClause, metadataTablePath));
                                        // print success message
                                        System.out.println("Data retrieved successfully");
                                        // get end time
                                        long endTime = System.currentTimeMillis();
                                        // calculate time taken
                                        long timeTaken = endTime - startTime;
                                        globalLogger.addLog(GENERAL_LOG, "Data retrieved from '" + tableName + "' successfully in " + timeTaken + " ms on " + userSession.getVirtualMachineName());
                                        globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(userSession.getSelectedDatabase()));
                                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + VALID_QUERY);
                                    } else if (queryParts[6].matches("[0-9a-zA-Z$_]+")) {
                                        queryClause = queryParts[6].toLowerCase(Locale.ROOT).replaceAll("[^0-9a-zA-Z$_]", "");
                                        System.out.println(fileHelper.selectedFileContent(FILE_PATH,content, tablePath, index, queryClause, metadataTablePath));
                                        // print success message
                                        System.out.println("Data retrieved successfully");
                                        // get end time
                                        long endTime = System.currentTimeMillis();
                                        // calculate time taken
                                        long timeTaken = endTime - startTime;
                                        globalLogger.addLog(GENERAL_LOG, "Data retrieved from '" + tableName + "' successfully in " + timeTaken + " ms on " + userSession.getVirtualMachineName());
                                        globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(userSession.getSelectedDatabase()));
                                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName()+ " " + query + VALID_QUERY);

                                    }
                                }
                            }
                        }
                    }
                } else {
                    errorMessage = "The table does not exists.";
                }
            } else {
                errorMessage = "There is an error in the query.";
            }
        } else {
            errorMessage = "The selected columns are not correct. Please check again.";
        }
        if(errorMessage.length()>0) {
            log.info(errorMessage);
            System.out.println(errorMessage);
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + INVALID_QUERY);
            return;
        }
    }

    public void executeDelete(String query, String[] queryParts) throws IOException {

        long startTime = System.currentTimeMillis();
        int index = -1;
        String errorMessage = "";
        String fromKeyword = queryParts[1].toLowerCase(Locale.ROOT);
        if (fromKeyword.equals(FROM)) {

            String tableName = queryParts[2].toLowerCase(Locale.ROOT);
            String tablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName + "/" + tableName + ".txt";
            String metadataTablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName + "/" + tableName + "_metadata.txt";
            if (fileHelper.isDirectoryExists(FILE_PATH,tablePath)) //If table exists
            {
                String whereCondition = queryParts[3].toLowerCase(Locale.ROOT);
                if (whereCondition.equals(WHERE)) {

                    List<String> headers = fileHelper.readFirstLine(FILE_PATH,metadataTablePath);
                    String whereClause = queryParts[4].toLowerCase(Locale.ROOT).replaceAll("[^0-9a-zA-Z$_]", "");

                    for (String header : headers) {
                        index++;
                        if (header.equals(whereClause)) {
                            String queryClause = queryParts[6].toLowerCase(Locale.ROOT).replaceAll("[^0-9a-zA-Z$_]", "");
                            fileHelper.deleteFromFile(FILE_PATH,tablePath, index, queryClause);
                            // print success message
                            System.out.println("Data deleted successfully");
                            // get end time
                            long endTime = System.currentTimeMillis();
                            // calculate time taken
                            long timeTaken = endTime - startTime;
                            globalLogger.addLog(GENERAL_LOG, "Data delete from '" + tableName + "' successfully in " + timeTaken + " ms on " + userSession.getVirtualMachineName());
                            globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(userSession.getSelectedDatabase()));
                            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " "  + userSession.getVirtualMachineName()+ " " + query + VALID_QUERY);
                            return;
                        } else {
                            errorMessage = "The column doesn't exist";
                        }
                    }
                } else {
                    errorMessage = "There is an error in the query";
                }
            } else {
                errorMessage = "The '" + tableName +"' doesn't exists";
            }
        } else {
            errorMessage = "There is an error in the query";
        }
        if(errorMessage.length()>0) {
            log.info(errorMessage);
            System.out.println(errorMessage);
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " "  + userSession.getVirtualMachineName()+ " " + query + INVALID_QUERY);
            return;
        }
    }

    private void createTable(StringTokenizer st, String query) {
        // get start time of the query
        long startTime = System.currentTimeMillis();
        StringBuilder columnsPart = new StringBuilder();
        String tableName;
        if(st.hasMoreTokens()) {
            tableName = st.nextToken();
        } else {
            System.out.println("Error in query: Table name not found");
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " "  + userSession.getVirtualMachineName() + " " + query + INVALID_QUERY);
            return;
        }
        String primaryKey = null;
        List<String> foreignKeysDataToWrite = new ArrayList<>();
        List<String> columnsDataToWrite = new ArrayList<>();
        StringBuilder constraintsPart = new StringBuilder();
        String[] individualConstraints;
        Pattern colWithoutKeyPattern = Pattern.compile("([0-9a-zA-Z$_]+)\\s+(varchar|int)(\\s+not\\s+null)?");
        Pattern colWithKeyPattern = Pattern.compile("([0-9a-zA-Z$_]+)\\s+(varchar|int)(\\s+primary\\s+key\\s+not\\s+null)");
        Pattern foreignKeyPattern = Pattern.compile("(foreign key)\\s*(\\()(\\s*[0-9a-zA-Z$_]+\\s*)(\\))\\s+(references)\\s+([0-9a-zA-Z$_]+)\\s*(\\()(\\s*[0-9a-zA-Z$_]+\\s*)(\\))");
        Pattern contentInsideBracketsPattern = Pattern.compile("\\(([^)]+)\\)");

        if (st.hasMoreTokens()) {
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                columnsPart.append(token).append(" ");
                if (token.contains(")")) {
                    break;
                }
            }
        } else {
            System.out.println("Error in query: Columns not found");
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName()+ " " + query + INVALID_QUERY);
            return;
        }
        // regex to check table columns
        Matcher matcher = contentInsideBracketsPattern.matcher(columnsPart);
        String columnsInsideParentheses = matcher.find() ? matcher.group(1) : null;
        String[] columns = null;
        if (columnsInsideParentheses != null) {
            columns = columnsInsideParentheses.split(",");

            // validate columns and data types
            for (String column : columns) {
                column = column.trim();
                // regex to check column name with data type
                Matcher matcher2 = colWithoutKeyPattern.matcher(column);
                Matcher matcher3 = colWithKeyPattern.matcher(column);
                if (matcher3.find()) {
                    if (primaryKey == null) {
                        primaryKey = matcher3.group(1);
                        column = column.replaceAll("primary\\s+key", "");
                        column = column.replaceAll("not\\s+null", "notnull");
                        column = column.replaceAll("\\s+", DELIMITER);
                    } else {
                        System.out.println("Table can have only one primary key");
                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + query + INVALID_QUERY);
                        return;
                    }
                }
                if (!matcher2.find()) {
                    System.out.println("There is an error in the query.");
                    globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + query + INVALID_QUERY);
                    return;
                } else {
                    column = column.replaceAll("not\\s+null", "notnull");
                    column = column.replaceAll("\\s+", DELIMITER);
                    columnsDataToWrite.add(column);
                }
            }

            // read rest of the query and store constraintsPart
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                constraintsPart.append(token).append(" ");
            }

            // check size of constraintsPart
            if (constraintsPart.length() > 0) {
                individualConstraints = constraintsPart.toString().split(",");

                for (String constraint : individualConstraints) {
                    constraint = constraint.trim();
                    Matcher matcher5 = foreignKeyPattern.matcher(constraint);
                    if (matcher5.find()) {
                        // remove all whitespaces greater than one from constraint
                        String constraintWithoutSpaces = constraint.replaceAll("\\s+", " ");
                        StringTokenizer st2 = new StringTokenizer(constraint, " ");
                        Matcher matcher6 = contentInsideBracketsPattern.matcher(constraintWithoutSpaces);
                        List<String> foreignKeyCols = new ArrayList<>();
                        while (matcher6.find()) {
                            foreignKeyCols.add(matcher6.group().replaceAll("(\\(|\\))", "").trim());
                        }
                        String foreignKeyTable = null;
                        while (st2.hasMoreTokens()) {
                            String token = st2.nextToken();
                            if (token.equals("references")) {
                                foreignKeyTable = st2.nextToken();
                            }
                        }
                        if (foreignKeyTable == null || foreignKeyCols.size() != 2) {
                            System.out.println("There is an error in the query.");
                            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " "  + userSession.getVirtualMachineName()+ " " + query +INVALID_QUERY);
                            return;
                        }
                        foreignKeysDataToWrite.add(FOREIGN_KEY + DELIMITER + foreignKeyCols.get(0) + DELIMITER + foreignKeyCols.get(1) + DELIMITER + foreignKeyTable + NEW_LINE);
                    }
                }
            }

            // create table directory
            String tablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName;
            if (fileHelper.createDirectory(FILE_PATH,tablePath)) {
                // create filepath string for table metadata
                String tableMetadataPath = tablePath + "/" + tableName + "_metadata";

                // create file to store table metadata
                if (fileHelper.createFile(FILE_PATH,tableMetadataPath)) {
                    metaDataHandler.addMetaData(userSession.getSelectedDatabase(),tableName,false);
                    // write table metadata to file
                    for (String colDataTypePair : columnsDataToWrite) {
                        fileHelper.appendDataToFile(FILE_PATH,tableMetadataPath + ".txt", colDataTypePair + NEW_LINE);
                    }
                    if (primaryKey != null) {
                        fileHelper.appendDataToFile(FILE_PATH,tableMetadataPath + ".txt", PRIMARY_KEY + DELIMITER + primaryKey + NEW_LINE);
                    } else {
                        System.out.println("There is an error in the query. No primary key specified.");
                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " "  + userSession.getVirtualMachineName() + " " + query +INVALID_QUERY);
                        return;
                    }
                    for (String foreignKey : foreignKeysDataToWrite) {
                        fileHelper.appendDataToFile(FILE_PATH,tableMetadataPath + ".txt", foreignKey);
                    }

                    // create filepath string for table data
                    String tableDataPath = tablePath + "/" + tableName;
                    // create file to store table data
                    if (fileHelper.createFile(FILE_PATH,tableDataPath)) {
                        // print success message
                        System.out.println("Table " + tableName + " created successfully");
                        // get end time
                        long endTime = System.currentTimeMillis();
                        // calculate time taken
                        long timeTaken = endTime - startTime;
                        globalLogger.addLog(GENERAL_LOG, "Table '" + tableName + "' created successfully in " + timeTaken + " ms on " + userSession.getVirtualMachineName());
                        globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(userSession.getSelectedDatabase()));
                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName()+ " " + query +VALID_QUERY);
                    } else {
                        System.out.println("Error in creating table");
                        globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName()+ " " + query +INVALID_QUERY);
                    }
                } else {
                    System.out.println("Error in creating table");
                    globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + INVALID_QUERY);
                }
            } else {
                System.out.println("Table already exists");
                globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName()+ " " + query + INVALID_QUERY);
            }
        } else {
            System.out.println("Error in query: Columns inside parentheses are not specified");
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName()+ " " + query + INVALID_QUERY);
        }
    }

    private void createDatabase(StringTokenizer st, String query) {
        // get start time
        long startTime = System.currentTimeMillis();
        if(st.hasMoreTokens()) {
            String databaseName = st.nextToken();
            String databasePath = DB_FILE_PATH + databaseName.trim();
            metaDataHandler.fetchGlobalMetaData();
            Database databaseStructure = metaDataHandler.checkDatabase(databaseName);
            String vm = databaseStructure.getVmName();
            if(vm.equalsIgnoreCase(userSession.getVirtualMachineName())){
                System.out.println("Database already available");
            }else if(!vm.equalsIgnoreCase(userSession.getVirtualMachineName()) && vm.length()>0){
                System.out.println("Database already available in "+vm);
            }else{
                boolean result = fileHelper.createDirectory(FILE_PATH,databasePath);
                if (result) {
                    System.out.println("Database created successfully");
                    metaDataHandler.addMetaData(databaseName,"",false);
                    // get end time
                    long endTime = System.currentTimeMillis();
                    // calculate time taken
                    long timeTaken = endTime - startTime;
                    globalLogger.addLog(GENERAL_LOG, "Database '" + databaseName + "' created successfully in " + timeTaken + " ms on " + userSession.getVirtualMachineName());
                    globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(databaseName));
                    globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + VALID_QUERY);
                }
            }
        } else {
            System.out.println("Error in query: Database name is not specified");
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + query + INVALID_QUERY);
        }
    }

    private void executeUpdate(StringTokenizer query, String queryString) {
        // get start time
        long startTime = System.currentTimeMillis();
        if (query.countTokens() < 5) {
            System.out.println("There is an error in the query.");
            globalLogger.addLog(QUERY_LOG, "@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + queryString);
        } else {
            String tableName = query.nextToken().toLowerCase(Locale.ROOT);
            String tablePath = DB_FILE_PATH + userSession.getSelectedDatabase() + "/" + tableName;
            String setColumnName = null;
            String setValue = null;
            String whereColumnName = null;
            String whereColumnValue = null;
            // check if table exists
            if (fileHelper.isDirectoryExists(FILE_PATH,tablePath)) {
                if (query.nextToken().equalsIgnoreCase(SET)) {
                    StringTokenizer st = new StringTokenizer(query.nextToken(), "=");
                    if (st.countTokens() == 2) {
                        setColumnName = st.nextToken().toLowerCase(Locale.ROOT);
                        setValue = st.nextToken();
                        if (query.nextToken().equalsIgnoreCase(WHERE)) {
                            StringTokenizer st2 = new StringTokenizer(query.nextToken(), "=");
                            if (st2.countTokens() == 2) {
                                whereColumnName = st2.nextToken().toLowerCase(Locale.ROOT);
                                whereColumnValue = st2.nextToken();
                            }
                        }
                    }
                }
                if (setColumnName != null && setValue != null && whereColumnName != null && whereColumnValue != null) {
                    // read table metadata
                    String tableMetadataPath = tablePath + "/" + tableName + "_metadata";
                    List<String> tableMetadata = fileHelper.readFileFromDirectoryToList(FILE_PATH,tableMetadataPath);
                    // check if whereColumnName exists in table metadata and find its whereColIndex
                    int whereColIndex = -1;
                    int setColIndex = -1;
                    // read tableMetadata line by line and find whereColIndex and setColIndex
                    for (int i = 0; i < tableMetadata.size(); i++) {
                        String metaDataLine = tableMetadata.get(i);
                        String[] metaData = metaDataLine.split(DELIMITER);
                        if (metaData[0].equalsIgnoreCase(whereColumnName)) {
                            whereColIndex = i;
                        }
                        if (metaData[0].equalsIgnoreCase(setColumnName)) {
                            setColIndex = i;
                        }
                        if(metaData[0].equalsIgnoreCase(PRIMARY_KEY) || metaData[0].equalsIgnoreCase(FOREIGN_KEY)) {
                            break;
                        }
                    }
                    if (whereColIndex != -1 && setColIndex != -1) {
                        // read table data
                        String tableDataPath = tablePath + "/" + tableName;
                        List<String> tableData = fileHelper.readFileFromDirectoryToList(FILE_PATH,tableDataPath);
                        // read entire file and update the value
                        for (int i = 0; i < tableData.size(); i++) {
                            String[] rowValues = tableData.get(i).split(DELIMITER);
                            if (rowValues[whereColIndex].equalsIgnoreCase(whereColumnValue)) {
                                rowValues[setColIndex] = setValue;
                            }
                            tableData.set(i, String.join(DELIMITER, rowValues));
                        }
                        // put tableData List into single String separated by new line
                        String tableDataString = String.join(NEW_LINE, tableData);
                        // write updated table data
                        fileHelper.writeFileToDirectory(FILE_PATH,tableDataPath, tableDataString);
                        // print success message
                        System.out.println("Successfully updated the value.");
                        // get end time
                        long endTime = System.currentTimeMillis();
                        // calculate total time taken
                        long totalTime = endTime - startTime;
                        globalLogger.addLog(GENERAL_LOG, "Updated the value in table '" + tableName + "' in " + totalTime + " ms on " + userSession.getVirtualMachineName());
                        globalLogger.addLog(GENERAL_LOG, DBState.getDBStatus(userSession.getSelectedDatabase()));
                        globalLogger.addLog(QUERY_LOG,"@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + queryString + VALID_QUERY);
                        return;
                    } else {
                        System.out.println("Column does not exist.");
                    }
                }
                System.out.println("Error: Invalid query.");
            } else {
                System.out.println("Table does not exist");
            }
            globalLogger.addLog(QUERY_LOG,"@" + userSession.getUserName() + " " + userSession.getSelectedDatabase() + " " + userSession.getVirtualMachineName() + " " + queryString + INVALID_QUERY);
        }
    }
}