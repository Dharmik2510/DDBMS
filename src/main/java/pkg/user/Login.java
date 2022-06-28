//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package pkg.user;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Stream;

import static pkg.utils.Constants.original;

public class Login {
    public static List<User> users = new ArrayList();

    public Login() {
    }

    public static List<User> login() throws IOException {
        User user = InputHandler.inputUserNameFromConsole();
        String userName = user.getHashedUserName();
        String password = user.getPassword();
        BufferedReader reader = new BufferedReader(new FileReader(original+"user/UserProfile.txt"));
        Scanner sc = new Scanner(System.in);

        String line;
        while((line = reader.readLine()) != null  ){
            if(!line.isEmpty()) {
                String[] inputs = line.split("\\~");
                String username = inputs[0];
                String pass = inputs[1];
                if (username.equals(userName) && password.equals(pass)) {
                    String security = inputs[2];
                    String ans = inputs[3];
                    System.out.println(security + "?");
                    String userInput = sc.nextLine();
                    if (ans.equals(userInput)) {
                        user.setLoggedIn(true);
                        users.add(user);
                    }
                }
            }
            else{
                return null;
            }

        }

        if(users.isEmpty()){
            return null;
        }
        return users;
    }

    public static List<User> logOut(List<User> userList) throws Exception {
        Iterator var1 = users.iterator();

        while(var1.hasNext()) {
            User user = (User)var1.next();
            if (user.isLoggedIn()) {
                user.setLoggedIn(false);
                Stream var10000 = Stream.generate(() -> {
                    return "=";
                }).limit(40L);
                PrintStream var10001 = System.out;
                Objects.requireNonNull(var10001);
                var10000.forEach(var10001::print);
                System.out.println("");
            }
        }

        return users;
    }
}
