package com.mimiclone.fips202.keccak;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * There are 7 permutation functions in the Keccak family.
 * <br/>
 * They are:
 * <ul>
 * <li>Keccak-f[25] (b=25, l=0, w=1)</li>
 * <li>Keccak-f[50] (b=50, l=1, w=2)</li>
 * <li>Keccak-f[100] (b=100, l=2, w=4)</li>
 * <li>Keccak-f[200] (b=200, l=3, w=8)</li>
 * <li>Keccak-f[400] (b=400, l=4, w=16)</li>
 * <li>Keccak-f[800] (b=800, l=5, w=32)</li>
 * <li>Keccak-f[1600] (b=1600, l=6, w=64)</li>
 * </ul>
 *
 */
public class Keccak {

    @Getter(value = AccessLevel.PACKAGE)
    public enum Permutation {
        KECCAK_F25,
        KECCAK_F50,
        KECCAK_F100,
        KECCAK_F200,
        KECCAK_F400,
        KECCAK_F800,
        KECCAK_F1600;

        private final int b, l, w, n;

        private Permutation() {
            l = ordinal();
            w = 2^l;
            b = 25 * w;
            n = 12 + 2*l;
        }
    }

    private static final long[] ROUND_CONSTANTS = {
            0x0000000000000001L, // RC[0]
            0x0000000000008082L, // RC[1]
            0x800000000000808AL, // RC[2]
            0x8000000080008000L, // RC[3]
            0x000000000000808BL, // RC[4]
            0x0000000080000001L, // RC[5]
            0x8000000080008081L, // RC[6]
            0x8000000000008009L, // RC[7]
            0x000000000000008AL, // RC[8]
            0x0000000000000088L, // RC[9]
            0x0000000080008009L, // RC[10]
            0x000000008000000AL, // RC[11]
            0x000000008000808BL, // RC[12]
            0x800000000000008BL, // RC[13]
            0x8000000000008089L, // RC[14]
            0x8000000000008003L, // RC[15]
            0x8000000000008002L, // RC[16]
            0x8000000000000080L, // RC[17]
            0x000000000000800AL, // RC[18]
            0x800000008000000AL, // RC[19]
            0x8000000080008081L, // RC[20]
            0x8000000000008080L, // RC[21]
            0x0000000080000001L, // RC[22]
            0x8000000080008008L, // RC[23]
    };

    private static final int[][] ROTATION_OFFSETS = {
            {0, 36, 3, 41, 18},
            {1, 44, 10, 45, 2},
            {62, 6, 43, 15, 61},
            {28, 55, 25, 21, 56},
            {27, 20, 39, 8, 14}
    };

    private final Permutation permutation;

    public Keccak(Permutation permutation) {
        this.permutation = permutation;
    }

    int safeMod(int val) {
        return val % 5 >= 0 ? val % 5 : val % 5 + 5;
    }

    public long[][] permute(long[][] a) {

        long[][] inputData = new long[5][5];
        for (int i = 0; i < 5; i++) {
            System.arraycopy(a[i], 0, inputData[i], 0, 5);
        }

        System.out.println("INPUT DATA");
        for (int i = 0; i < 5; i++) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < 5; j++) {
                builder.append(String.format("%016X ", inputData[j][i]));
            }
            System.out.println(builder.toString());
        }

        for (int i = 0; i < permutation.n; i++) {
            inputData = round(inputData, ROUND_CONSTANTS[i]);
        }

        return inputData;
    }

    private void printState(String label, long[][] data) {
        System.out.printf("After %s%n", label);
        for (int y = 0; y < 5; y++) {
            StringBuilder builder = new StringBuilder();
            for (int x = 0; x < 5; x++) {
                builder.append(String.format(" %016X", data[x][y]));
            }
            System.out.println(builder.toString());
        }
        System.out.println();
    }

    public long[][] round(long[][] a, long rc) {

        // STEP 1 (Theta)
        long[] c = new long[5];
        for (int x = 0; x < 5; x++) {
            c[x] = a[x][0] ^ a[x][1] ^ a[x][2] ^ a[x][3] ^ a[x][4];
        }

        long[] d = new long[5];
        for (int x = 0; x < 5; x++) {
            d[x] = c[safeMod(x-1)] ^ Long.rotateLeft(c[safeMod(x+1)], 1);
        }

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                a[x][y] = a[x][y] ^ d[x];
            }
        }

        printState("Theta", a);

        // STEP 2 (Rho) and STEP 3 (Pi)
        long[][] b = new long[5][5];
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                b[y][safeMod(safeMod(2*x)+safeMod(3*y))] = Long.rotateLeft(a[x][y], ROTATION_OFFSETS[x][y]);
            }
        }

        printState("Rho and Pi", a);

        // STEP 4 (Chi)
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                a[x][y] = b[x][y] ^ ((~b[safeMod(x+1)][y]) & b[safeMod(x+2)][y]);
            }
        }

        printState("Chi", a);

        // STEP 5 (Iota)
        a[0][0] = a[0][0] ^ rc;

        printState("Iota", a);

        return a;

    }

}
