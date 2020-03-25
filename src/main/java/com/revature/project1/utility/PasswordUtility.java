package com.revature.project1.utility;


import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class PasswordUtility {

    private PasswordUtility() {
        super();
    }

    public static String sha512Hash(String in) {
        Hasher hasher = Hashing.sha512().newHasher();
        hasher.putString(in, StandardCharsets.UTF_8);
        HashCode out = hasher.hash();
        return out.toString();
    }
}
