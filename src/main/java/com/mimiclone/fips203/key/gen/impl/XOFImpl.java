package com.mimiclone.fips203.key.gen.impl;

import com.mimiclone.fips203.key.gen.KeyPairGenerationException;
import com.mimiclone.fips203.key.gen.XOF;
import com.mimiclone.fips203.key.gen.XOFContext;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class XOFImpl implements XOF {

    @Override
    public XOFContext initContext() {

        XOFContext ctx = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHAKE128");
            ctx = new XOFContextImpl(md);
        } catch (NoSuchAlgorithmException e) {
            throw new KeyPairGenerationException(e.getMessage());
        }

        return ctx;

    }

    @Override
    public XOFContext absorb(XOFContext ctx, String[] lines) {

        for (String line : lines) {
            ctx.getMessageDigest().update(line.getBytes());
        }

        return ctx;

    }

    @Override
    public byte[] squeeze(XOFContext ctx, int[] byteCounts) {
        int outputSize = Arrays.stream(byteCounts).sum();
        byte[] output = new byte[32];
        try {
            for (int byteCount : byteCounts) {
                ctx.getMessageDigest().digest(output, 0, 8 * byteCount);
            }
        } catch (DigestException e) {
            throw new KeyPairGenerationException(e.getMessage());
        }

        return output;

    }
}
