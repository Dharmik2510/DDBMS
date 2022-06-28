package pkg.user;

import pkg.logger.GlobalLogger;
import pkg.metadata.Config;
import pkg.metadata.ReadConfig;
import pkg.queryengine.QueryEngine;
import pkg.utils.Constants;
import pkg.utils.FileHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static pkg.utils.Constants.original;

public class UserRunner {
    public static User user;
    FileHelper fileHelper ;
    Logger log;
    GlobalLogger globalLogger ;
    public void start() throws Exception {
        ReadConfig readConfig = new ReadConfig();
        Config config = readConfig.read();
        Constants.setFilePath(config.getVmResources());
        Constants.setOriginal(config.getVmResources());
        Constants.setTemp(config.getTempVmResources());
        List<User> userList;
        fileHelper = new FileHelper();
        log = Logger.getLogger(UserRunner.class.getName());
        globalLogger = new GlobalLogger(fileHelper,log);
        globalLogger.addLog("event","Application started");
        while(true) {
            Stream.generate(() -> "=").limit(52).forEach(System.out::print);
            System.out.println("\n");
            System.out.println("Enter your choice:");
            System.out.println("1. Register \n2. Login \n3. Logout \n4. Exit Application");
            System.out.print("> ");
            Scanner sc = new Scanner(System.in);
            String userInput = sc.next();
            if (userInput.equals("exit")) {
                globalLogger.addLog("event","Application stopped");
                System.out.println("Application stopped");
                break;
            }

            String option = userInput;
            Stream.generate(() -> "=").limit(52).forEach(System.out::print);

            userList = userChoice(option);

            if(userList==null||userList.isEmpty()){
                System.out.println("Enter details correctly");
                continue;
            }

            if(userList.get(0).isLoggedIn()){
                this.user = userList.get(0);
                //after logging in
                    try {
                        OptionRunner optionRunner = new OptionRunner(UserRunner.user);
                        optionRunner.optionRunner();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw e;
                    }
            }
        }

    }

    public List<User> userChoice(String choice) throws Exception {
        List<User> users = new ArrayList<>();
           switch (choice) {
               case "1":
                   //register users
                   users = Register.registerUser(users);
                   if(users==null){
                      break;
                   }
                   globalLogger.addLog("event",users.get(0).getUserName()+" has completed registration");
                   break;
               case "2":
                   //user login
                   users=Login.login();
                   if(users!=null) {
                       globalLogger.addLog("event", users.get(0).getUserName() + " has logged in");
                   }
                   break;
               case "3":
                   //user log out
                   users = Login.logOut(users);
                   globalLogger.addLog("event",users.get(0).getUserName()+" has logged out");
                   System.exit(0);
                   break ;
               case "4":
                   //user log out
                   System.exit(0);
                   break ;
               default:
                   globalLogger.addLog("event","Application stopped because of wrong choice");
                   System.exit(0);
                   break ;

           }
        return users;
    }
}
