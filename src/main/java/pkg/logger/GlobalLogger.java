package pkg.logger;

import pkg.utils.FileHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import static pkg.utils.Constants.*;

public class GlobalLogger {
    FileHelper fileHelper;
    Logger logger;

    public GlobalLogger(FileHelper fileHelper, Logger logger) {
        this.fileHelper = fileHelper;
        this.logger = logger;
    }

    public void addLog(String logType, String logMessage) {
        String logFilePath;
        switch (logType) {
            case QUERY_LOG:
                logFilePath = LOGS_FILE_PATH + QUERY_LOG_FILE_NAME;
                break;
            case EVENT_LOG:
                logFilePath = LOGS_FILE_PATH + EVENT_LOG_FILE_NAME;
                break;
            case GENERAL_LOG:
                logFilePath = LOGS_FILE_PATH + GENERAL_LOG_FILE_NAME;
                break;
            default:
                logFilePath = null;
        }
        if(logFilePath != null) {
            if(!fileHelper.isFileExists(original,logFilePath)) {
                fileHelper.createFile(original,logFilePath);
            }
            logger.info(logMessage);
            LocalDate currentDate = LocalDate.now();
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String currentTimeFormatted = currentDateTime.format(formatter);
            String logFileContent = "[" + currentDate + " | " + currentTimeFormatted + "] " + logMessage + "\n";
            fileHelper.appendDataToFile(original,logFilePath + ".txt", logFileContent);
        }
    }
}
