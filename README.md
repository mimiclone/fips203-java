# fips203-java
Java-based implementation of the final NIST FIPS-203 Standard (Module-Lattice Key Encapsulation Mechanism)
which was released on August 13, 2024 https://csrc.nist.gov/pubs/fips/203/final

# WARNING: This implementation is currently _insecure_ against memory map and timing side-channel attacks (See Issues #1 and #2).
## This warning will not be removed until those issues have been remediated and verified by the reporters.
_While this implementation conforms with the algorithm specifications and passes tests with NIST-provided test vectors,
it has not been certified by any NIST certified testing lab and should not be used in production systems or by government
agencies that need to comply with the standard_

Code is provided under the MIT License and is made available *as is*, without any warranty express or implied.

## So Why Does It Exist?
Quite simply, we wanted our apps to have the highest level of end-to-end messaging encryption, and there just aren't
any Java libraries available that implement the new key exchange mechanism from the final standard.  We have made it 
available for others who may be in the same boat, or are just interested in learning about lattice encryption.  The code
is heavy on explanatory comments that were written while parsing through the details of the standard.

## Usage

The ML-KEM (Module Lattice Key Encapsulation Mechanism) is meant to replace existing DHKEM (Diffie-Hellman Key Exchange Mechanism)
to ensure that this exchange is resistant to attacks from quantum computers.  At some point, the major providers of
internet security will implement this mechanism at the transport-level, making all internet traffic secure by default.
However, we expect this to take a while, so in the meantime libraries like this one can be used at the application level
to provide the same level of security.

The fundamental idea is that two parties (by convention referred to as Alice and Bob) want to exchange data securely.
Symmetric Ciphers (such as AES-256) are much faster than Asymmetric Ciphers (those with a private and public keypair), 
but require that both parties have a shared secret key.  ML-KEM provides a way for Alice and Bob to use asymmetric key
pair to securely exchange a shared key for a strong Symmetric Cipher.  Effectively, we are using slow asymmetric encryption
to bootstrap fast symmetric encryption between two parties.

It works like this:
1. Alice indicates to Bob through some insecure mechanism (a Slack message, a phone call, etc.) that she would like to exchange messages securely
2. Bob generates a KeyPair (in this case an EncapsulationKey and a DecapsulationKey).  He keeps the DecapsulationKey private, and shares the EncapsulationKey with Alice over an insecure channel.
3. Alice uses Bob's EncapsulationKey to encapsulate a SharedSecretKey into a CipherText.  She keeps the SharedSecretKey private and sends Bob back the CipherText.
4. Bob uses the CipherText and his private DecapsulationKey to derive the same SharedSecretKey that Alice already has.
5. Bob and Alice can then use a standard Cipher (i.e. AES-256) to communicate quickly over a network.

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
    
    KeyPair keyPair = fips203.generateKeyPair();
```

### Encapsulation
Call the encapsulate() method from the interface and pass it the encapsulation key.  The resulting
Encapsulation object contains both a SharedSecretKey and a CipherText.  The SharedSecretKey is a 32-byte
value that should be kept safe on the platform and can be used with any 256-bit cipher such as AES-256.
The CipherText is an encrypted value that can be shared across the network and will allow someone with
a matching DecapsulationKey to derive the same shared secret.

```java
    import com.mimiclone.fips203.encaps.Encapsulation;
    
    Encapsulation encapsulation = fips203.encapsulate(keyPair.getEncapsulationKey());
```

### Decapsulation
Call the decapsulate() method from the interface and pass in a DecapsulationKey and CipherText.  The DecapsulationKey
must match the EncapsulationKey that was used to generate the CipherText.  In almost all cases this CipherText will
be received over a network as raw bytes and will need to be wrapped in an MLKEMCipherText instance before use.

```java
    import com.mimiclone.fips203.key.DecapsulationKey;
    import com.mimiclone.fips203.message.CipherText;
    
    // TODO: Your code to receive the cipherText bytes from a client on the network.
    
    CipherText cipherText = MLKEMCipherText.create(cipherTextBytes);
    Encapsulation encapsulation = fips203.encapsulate(keyPair.getEncapsulationKey(keyPair.getDecapsulationKey(), cipherText));
```

# NOTES

This implementation also includes a partial implementation of the FIPS 202 specification,
specifically the SHAKE128 and SHAKE256 XOF (eXtended Output Functions).  The FIPS 202 standard defined
the SHA3 family of six functions, which includes four standard hashes (SHA3-224, SHA3-256, SHA3-384 and SHA3-512) and
two extendable-output-functions(XOFs) called SHAKE128 and SHAKE256.  For some reason, nearly every security provider that
implemented FIPS 202 exposed the four standard hashes, but did not expose the XOF functions.  From digging in comments
in the JDK source this appears to have been an issue of a mismatch between the way the Java Security MessageDigest
was designed and the way these XOFs were supposed to work.

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
also guarantees that this code cannot be certified to adhere to the standard in its current form.

# Resources

PQC (Post-Quantum Cryptography) is a complex topic.  Part of our reasoning for making this repository public is to help
others who may be muddling through all these new standards and requirements.  It is common practice for security libraries
to be published without explanatory comments and with most or all of the source history removed.  We have chosen explicitly
to leave these in to try and be helpful to others.  Still, you will likely need to read through several implementations
and a number of documents to fully grasp what is happening under the hood here.  Some of the resources we found helpful
are linked below.

1. The [FIPS 203 Final Standard] https://csrc.nist.gov/pubs/fips/203/final published on August 13, 2024
2. The [FIPS 202 Final Standard] https://csrc.nist.gov/pubs/fips/202/final published August 2015
3. The [ACVP-Server Repository] https://github.com/usnistgov/ACVP-Server is where all the "golden reference" implementations of the specs are kept.  This is also where we got our test vectors, and the project is used to produce new test vectors for certification of implementations.  This repository is an absolute gold-mine, although sadly the reference implementations are written in C# and run on the .NET platform, so frequently the algorithms don't follow step-by-step those outlined in the standards to get around some of the foibles of that platform.
3. The [PQC Forum Mailing List] https://groups.google.com/a/list.nist.gov/g/pqc-forum which is where people working on FIPS 203 and related standards tend to exchange ideas.
4. [JOSE] https://jose.readthedocs.io/en/latest/ is a related standard that is currently incorporating PQC.
5. [COSE] https://www.w3.org/TR/vc-jose-cose/ is a related standard that is currently incorporating PQC.
6. [SwiftKyber] https://github.com/leif-ibsen/SwiftKyber is a very clean and well-maintained implementation of FIPS 203 by Leif Ibsen that can be used in an iOS application.
7. [KekkakJ] https://github.com/aelstad/keccakj is an out-of-date but reasonably well-written implementation of FIPS 202 in Java.
