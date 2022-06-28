package pkg.user;

import pkg.analytics.Analytics;
import pkg.logger.GlobalLogger;
import pkg.metadata.Config;
import pkg.metadata.MetaDataHandler;
import pkg.metadata.ReadConfig;
import pkg.queryengine.QueryEngine;
import pkg.runners.DataModelRunner;
import pkg.runners.ExportRunner;
import pkg.transaction.TransactionManager;
import pkg.usersession.UserSession;
import pkg.utils.FileHelper;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import static pkg.user.UserRunner.user;

public class OptionRunner {
   private User user;
    FileHelper fileHelper ;
    Logger log;
    GlobalLogger globalLogger ;
    TransactionManager transactionManager;
    MetaDataHandler metaDataHandler;
    public OptionRunner(User user) {
        this.user = user;
    }

    public void optionRunner() {
        ReadConfig readConfig = new ReadConfig();
        Config config = readConfig.read();
        fileHelper = new FileHelper();
        log = Logger.getLogger(UserRunner.class.getName());
        globalLogger = new GlobalLogger(fileHelper,log);
        transactionManager = new TransactionManager();
        metaDataHandler = new MetaDataHandler(config);
        UserSession userSession = new UserSession();
        userSession.setUserName(user.getUserName());
        userSession.setVirtualMachineName(config.getVmName());
        System.out.println(userSession.getUserName() +"  is logged in.");

        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Select an option (Enter the option number) : ");
            System.out.println("1. Query Editor \n2. Export SQL Dump \n3. Data Model \n4. Analytics \n5. Go Back to Main Menu");
            System.out.print("> ");
            int option = scanner.nextInt();
            boolean result = false;
            switch (option) {
                case 1:
                    globalLogger.addLog("event","Query Editor option selected");
                    result=true;
                    QueryEngine queryEngine = new QueryEngine(userSession, fileHelper, globalLogger,transactionManager,metaDataHandler);
                    try {
                        // flush scanner
                        scanner.nextLine();
                        queryEngine.executeQuery(scanner);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    globalLogger.addLog("event","Export SQL Dump option selected");
                    result=true;
                    ExportRunner exportRunner = new ExportRunner();
                    try {
                        // flush scanner
                        scanner.nextLine();
                        exportRunner.exportRunner(scanner);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    globalLogger.addLog("event","Data Modelling option selected");
                    result=true;
                    DataModelRunner dataModelRunner = new DataModelRunner();
                    try {
                        // flush scanner
                        scanner.nextLine();
                        dataModelRunner.dataModelRunner(scanner);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 4:
                    globalLogger.addLog("event","Analytics option selected");
                    result=true;
                    Analytics analytics = new Analytics();
                    try {
                        analytics.executeQuery();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 5:
                    System.out.println("Going back to main menu!!");
                    globalLogger.addLog("event","Going back to main menu");
                    result=false;
                    break;
                default:
                    System.out.println("\nWrong input. Please try again!\n");
                    globalLogger.addLog("event","Wrong option selected");
                    break;
            }

            if(result==false){break;}
        }

    }
}
