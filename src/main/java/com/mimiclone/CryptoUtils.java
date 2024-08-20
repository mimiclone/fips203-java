package com.mimiclone;

public class CryptoUtils {

    public static int mod(int val, int base) {
        return (val % base + base) % base;
    }

}
