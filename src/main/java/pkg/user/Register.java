package pkg.user;

import pkg.utils.FileHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static pkg.utils.Constants.FILE_PATH;
import static pkg.utils.Constants.original;

public class Register {
    public static List<User> registerUser(List<User> userList) throws IOException {
        User user = InputHandler.inputFromConsole();
        FileHelper helper = new FileHelper();
        if(!helper.isFileExists(original,"user/UserProfile.txt")){
            helper.createDirectory(original,"user");
            helper.createFile(original,"user/UserProfile");
        }

        if (!FileHelper.checkName(user.getHashedUserName(),user.getPassword())) {
            System.out.println("username already exists");
            System.out.println("Login to continue");
            return null;
        }
        try {
            userList.add(user);
            boolean result = Register.addUserIntoFile(userList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Stream.generate(() -> "=").limit(52).forEach(System.out::print);
        System.out.println("\n");
        System.out.println("User: " + user.getUserName() + " :registered");
        System.out.println("\n");
        return userList;
    }

    public static boolean addUserIntoFile(List<User> userList) throws IOException {
        FileHelper fileHelper = new FileHelper();
        BufferedReader br = new BufferedReader(new FileReader(original+"user/UserProfile.txt"));
        String line;
        boolean result= true;

        for (User users : userList) {
            String content = users.getHashedUserName() + "~" + users.getPassword() + "~" + users.getSecurityQuestion() + "~" + users.getAnswer();
            if (br.readLine() == null) {
                fileHelper.writeFileToDirectory(original,"user/UserProfile",content);
            }
            else {
                fileHelper.appendDataToFile(original,"user/UserProfile.txt", "\n" + content);

            }
        }
        return result;
    }
}
