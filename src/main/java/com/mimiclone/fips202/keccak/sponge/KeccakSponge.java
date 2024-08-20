package com.mimiclone.fips202.keccak.sponge;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.nio.ByteBuffer;
import java.util.BitSet;

import static com.mimiclone.CryptoUtils.mod;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class KeccakSponge {

    // NOTE: We are only implementing Keccak[c] where c=512 because we only need
    // the SHAKE256 XOF function to support FIPS203.  This version of the Keccak Sponge
    // does not accept any parameters.  The original spec operates on bit strings, but
    // the specific rate and capacity we support will only use bitstrings that align
    // on byte boundaries.

    static final int RATE_BITS = 1088;
    static final int RATE_BYTES = 136;
    static final int CAPACITY_BITS = 512;
    static final int CAPACITY_BYTES = 64;

    public static KeccakSponge create() {
        return new KeccakSponge();
    }

    // NOTE: This implementation assumes we will always pad a message, even if the message
    // falls exactly on a rate boundary
    byte[] pad(int messageLengthInBytes) {

        int paddingBytes = RATE_BYTES - mod(messageLengthInBytes, RATE_BYTES);

        // Message does not need padding
        if (paddingBytes == 0) {
            if (messageLengthInBytes >= RATE_BYTES) {
                return new byte[0];
            } else {
                return new byte[] {
                        (byte)0x80L,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00,
                        0x01,
                };
            }

        }

        // Message needs single padding byte
        else if (paddingBytes == 1) {
            return new byte[]{
                    (byte) 0b10000001L
            };
        }

        // Message needs at least two padding bytes
        else if (paddingBytes > 1) {
            byte firstByte = (byte) 0b10000000L;
            byte middleByte = (byte) 0b00000000L;
            byte lastByte = (byte) 0b00000001L;
            ByteBuffer buffer = ByteBuffer.allocate(paddingBytes);
            buffer.put(firstByte);
            if (paddingBytes > 2) {
                for (int i = 0; i < paddingBytes - 2; i++) {
                    buffer.put(middleByte);
                }
            }
            buffer.put(lastByte);
            return buffer.array();
        }

        // Something went wonky, our modulus operation returned a negative number
        else {
            throw new IllegalStateException("Modulus operation returned a negative value: %d".formatted(paddingBytes));
        }

    }

}
