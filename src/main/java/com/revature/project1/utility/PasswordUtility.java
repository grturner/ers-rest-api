package com.revature.project1.utility;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class PasswordUtility {
    private static String salt = null;
    private static PasswordUtility instance = null;

    private PasswordUtility() {
        setSalt();
    }

    public PasswordUtility getInstance() {
        if (instance == null) {
            instance = new PasswordUtility();
        }
        return instance;
    }

    private void setSalt() {
        //TODO pull salt in from config file
        this.salt = "DatabaseSalt";
    }

    public String hashPassword(String password) {
        return Hashing.sha512().hashString(password.concat(salt), StandardCharsets.UTF_8).toString();
    }
}
