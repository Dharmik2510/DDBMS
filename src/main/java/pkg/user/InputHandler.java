//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package pkg.user;

import java.io.IOException;
import java.util.Scanner;

public class InputHandler {
    public InputHandler() {
    }

    public static User inputFromConsole() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter Username Here:");
        String userInput = sc.nextLine();
        String userName = userInput;
        System.out.println("Enter password Here:");
        userInput = sc.nextLine();
        String hashedUserID = Hashing.username(userName);
        String hashedPassword = Hashing.password(userInput);
        System.out.println("Enter security question Here:");
        userInput = sc.nextLine();
        String security = userInput;
        System.out.println("Enter answer Here:");
        userInput = sc.nextLine();
        return createUserProfile(userName, hashedPassword, security, userInput, hashedUserID);
    }

    public static User createUserProfile(String userName, String passWord, String securityQuestion, String answer, String hashedUserName) {
        return new User(userName, passWord, securityQuestion, answer, hashedUserName);
    }

    public static User inputUserNameFromConsole() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\nEnter Username Here:");
        String userInput = sc.nextLine();
        String userName = userInput;
        System.out.println("Enter password Here:");
        userInput = sc.nextLine();
        String hashedUserID = Hashing.username(userName);
        String hashedPassword = Hashing.password(userInput);
        return new User(userName, hashedPassword, hashedUserID);
    }
}
