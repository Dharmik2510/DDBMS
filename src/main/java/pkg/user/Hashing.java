//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package pkg.user;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    static MessageDigest messageDigest;

    public Hashing() {
    }

    public static String username(String username) {
        return getUsername(username);
    }

    public static String getUsername(String username) {
        return hashedValue(username);
    }

    public static String password(String password) {
        return getPassword(password);
    }

    public static String getPassword(String password) {
        return hashedValue(password);
    }

    private static String hashedValue(String rawData) {
        messageDigest.update(rawData.getBytes());
        byte[] digest = messageDigest.digest();
        BigInteger no = new BigInteger(1, digest);

        String hashText;
        for(hashText = no.toString(16); hashText.length() < 32; hashText = "0" + hashText) {
        }

        return hashText;
    }

    static {
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException var1) {
            var1.printStackTrace();
        }

    }
}
