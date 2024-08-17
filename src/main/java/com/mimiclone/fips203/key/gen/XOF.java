package com.mimiclone.fips203.key.gen;

public interface XOF {

    XOFContext initContext();

    XOFContext absorb(XOFContext ctx, String[] lines);

    byte[] squeeze(XOFContext ctx, int[] byteCounts);
}
