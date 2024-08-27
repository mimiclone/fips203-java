package com.mimiclone.fips203.hash;

public interface Hash {

    byte[] prfEta1(byte[] s, byte b);

    byte[] prfEta2(byte[] s, byte b);

    byte[] gHash(byte[] c);

    byte[] hHash(byte[] s);

    byte[] jHash(byte[] s);

}
