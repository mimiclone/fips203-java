package com.mimiclone;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CryptoUtils {

    public static final int[] INT_BIT_MASKS = new int[]{
            0x0, 0x1, 0x3, 0x7, 0xF, 0x1F, 0x3F, 0x7F, 0xFF, 0x1FF, 0x3FF, 0x7FF, 0xFFF, 0x1FFF, 0x3FFF, 0x7FFF, 0xFFFF
    };

    public static int mod(int val, int base) {
        return (val % base + base) % base;
    }

    public static long bytesToLong(ByteOrder order, byte[] bytes, int offset) {

        byte[] modBytes = new byte[] {
                bytes[offset], bytes[offset+1], bytes[offset+2], bytes[offset+3],
                bytes[offset+4], bytes[offset+5], bytes[offset+6], bytes[offset+7]
        };

        ByteBuffer buffer = ByteBuffer.wrap(modBytes);
        buffer.order(order);
        return buffer.getLong();

    }

    public static void longsToBytes(ByteOrder order, long[] input, int inputOffset, byte[] output, int outputOffset, int length) {

        // Iterate over the longs
        for (int i = 0; i < length; ++i) {
//            byte[] inputBytes = longToBytes(order, input[inputOffset+i] );
            outputOffset += 8;
        }

    }

}
