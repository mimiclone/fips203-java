package com.mimiclone.fips203.key.gen.impl;

import com.mimiclone.fips203.key.gen.XOFContext;

import java.security.MessageDigest;

public class XOFContextImpl implements XOFContext {

    private final MessageDigest digest;

    XOFContextImpl(MessageDigest md) {
        digest = md;
    }

    @Override
    public MessageDigest getMessageDigest() {
        return digest;
    }
}
