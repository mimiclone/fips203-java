# fips203-java
Java-based implementation of the NIST FIPS-203 Standard Quantum Safe Encryption Algorithm

*This implementation is under development!*

_It has not been certified by any NIST certified testing lab and should not be used to build products for government
agencies that need to comply with the standard_

## So Why Does It Exist?
Quite simply, we wanted our apps to have the highest level of end-to-end messaging encryption, and there just aren't
any libraries available written in Java that implement the new standard.  We have made it available for other who may be
in the same boat, or are just interested in learning about lattice encryption.

### Getting an Instance
You'll need to choose a parameter set (ML_KEM_512, ML_KEM_768, ML_KEM_1024), which is the
only initialization parameter needed.

```java
    import com.mimiclone.fips203.FIPS203;
    import com.mimiclone.fips203.MimicloneFIPS203;
    import com.mimiclone.fips203.ParameterSet;
    
    // For ML_KEM_512
    FIPS203 fips203 = MimicloneFIPS203(ParameterSet.ML_KEM_512);

    // For ML_KEM_768
    FIPS203 fips203 = MimicloneFIPS203(ParameterSet.ML_KEM_768);
    
    // For ML_KEM_1024
    FIPS203 fips203 = MimicloneFIPS203(ParameterSet.ML_KEM_1024);
```

### Key Pair Generation
Simply call the generateKeyPair() method from the interface.

```java
    import com.mimiclone.fips203.key.KeyPair;
    
    FIPS203KeyPair keyPair = fips203.generateKeyPair();
```

# NOTES

This implementation also includes a partial implementation of the FIPS 202 specification,
specifically the SHAKE128 and SHAKE256 XOF (eXtended Output Functions).  We had no intention
of implementing FIPS 202 initially, but for some reason beyond comprehension the various
JDK maintainers have decided to make these available only for use as stream ciphers and
did not see fit to expose their functionality as an XOF, which is required by FIPS 203.

To make this implementation work without having to register the module as a JCA Security Provider
and abuse a stream cipher as if it were an XOF, we have simply implemented the two algorithms we
need in the manner required by FIPS 203.  We have very intentionally *not* implemented these
algorithms in compliance with the FIPS 202 standard, which requires they operate on bit strings
instead of bytes because the standard is over a decade old and needed to be compatible with
a lot of other schemes.  FIPS 203, however, makes use of these functions *as if* they only
operate on byte arrays, and in fact *requires* the use of an XOF wrapper to hide the fact
that SHAKE128 and SHAKE256 operate on bit strings.  So for our purposes, we have simply cut
out all the shenanigans and implemented SHAKE128 and SHAKE256 to operate directly on bytes.
This makes the implementation significantly cleaner, simpler and easier to understand, but
also guarantees that this code cannot be certified to adhere to the standard.