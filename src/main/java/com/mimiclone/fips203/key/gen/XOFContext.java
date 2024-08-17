package com.mimiclone.fips203.key.gen;

import java.security.MessageDigest;

public interface XOFContext {

    MessageDigest getMessageDigest();

}
