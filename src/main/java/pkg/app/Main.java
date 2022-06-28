package pkg.app;

import pkg.user.UserRunner;

public class Main {
    public static void main(String[] args) throws Exception {
        //help user to login and register
        try {
            UserRunner userRunner = new UserRunner();
            userRunner.start();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }


    }
}
