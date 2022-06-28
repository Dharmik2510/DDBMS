package pkg.utils;

public class Constants {
    // Global Files path
//    public static String FILE_PATH = "src/main/resources/";
//    public static String original = "src/main/resources/";
    public static String FILE_PATH = "src/main/resources/";
    public static String original = "src/main/resources/";
    public static String temp = "src/main/resources/temp/";

    // QueryEngine constants
    public static final String DB_FILE_PATH = "databases/";
    public static final String CREATE = "create";
    public static final String INSERT = "insert";
    public static final String SELECT = "select";
    public static final String DELETE = "delete";
    public static final String UPDATE = "update";
    public static final String USE = "use";
    public static final String TABLE = "table";
    public static final String DATABASE = "database";
    public static final String INTO = "into";
    public static final String VALUES = "values";
    public static final int VALUES_INDEX = 4;
    public static final String FROM = "from";
    public static final String WHERE = "where";
    public static final String SET = "set";
    public static final String DELIMITER = "~";
    public static final String NEW_LINE = "\n";
    public static final String PRIMARY_KEY = "PK";
    public static final String FOREIGN_KEY = "FK";
    public static final String QUIT = "quit";

    // GlobalLogger constants
    public static final String QUERY_LOG = "query";
    public static final String EVENT_LOG = "event";
    public static final String GENERAL_LOG = "general";
    public static final String LOGS_FILE_PATH = "logs/";
    public static final String QUERY_LOG_FILE_NAME = "query.log";
    public static final String EVENT_LOG_FILE_NAME = "event.log";
    public static final String GENERAL_LOG_FILE_NAME = "general.log";
    public static final String VALID_QUERY = " :valid";
    public static final String INVALID_QUERY = " :invalid";

    public static void setFilePath(String filePath) {
        Constants.FILE_PATH = filePath;
    }

    public static void setOriginal(String original) {
        Constants.original = original;
    }

    public static void setTemp(String temp) {
        Constants.temp = temp;
    }
}
