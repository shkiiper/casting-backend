package com.casting.platform.util;

import java.security.SecureRandom;

public class CodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    public static String generate6Digit() {
        int number = 100000 + random.nextInt(900000);
        return String.valueOf(number);
    }
}