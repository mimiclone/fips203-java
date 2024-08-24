package com.mimiclone.fips203.shake;

public interface XOF extends SHA {

    public void absorb(byte[] message, int bitLength);

    public void squeeze(byte[] output, int outputBitLength);

}
