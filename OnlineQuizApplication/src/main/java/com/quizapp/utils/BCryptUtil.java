package com.quizapp.utils;

import org.mindrot.jbcrypt.BCrypt;

public class BCryptUtil {
    public static boolean checkpw(String plaintext, String hashed) {
        return BCrypt.checkpw(plaintext, hashed);
    }

    public static String hashpw(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt());
    }

    public static String gensalt() {
        return BCrypt.gensalt();
    }
}
