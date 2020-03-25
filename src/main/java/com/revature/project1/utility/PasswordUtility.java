package com.revature.project1.utility;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class PasswordUtility {
    public static String sha512Hash(String in) {
        Hasher hasher = Hashing.sha512().newHasher();
        hasher.putString(in, Charsets.UTF_8);
        HashCode out = hasher.hash();
        return out.toString();
    }
}
