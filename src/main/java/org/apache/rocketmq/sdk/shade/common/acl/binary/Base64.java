package org.apache.rocketmq.sdk.shade.common.acl.binary;

import org.apache.rocketmq.sdk.shade.client.Validators;
import org.apache.rocketmq.sdk.shade.common.acl.common.StringUtils;
import java.math.BigInteger;

import org.springframework.beans.PropertyAccessor;

public class Base64 extends BaseNCodec {
    private static final int BITS_PER_ENCODED_BYTE = 6;
    private static final int BYTES_PER_UNENCODED_BLOCK = 3;
    private static final int BYTES_PER_ENCODED_BLOCK = 4;
    private final byte[] encodeTable;
    private final byte[] decodeTable;
    private final byte[] lineSeparator;
    private final int decodeSize;
    private final int encodeSize;
    static final byte[] CHUNK_SEPARATOR = {13, 10};
    private static final byte[] STANDARD_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
    private static final byte[] URL_SAFE_ENCODE_TABLE = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
    private static final int MASK_6BITS = 63;
    private static final byte[] DECODE_TABLE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, MASK_6BITS, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, MASK_6BITS, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};

    public Base64() {
        this(0);
    }

    public Base64(boolean urlSafe) {
        this(76, CHUNK_SEPARATOR, urlSafe);
    }

    public Base64(int lineLength) {
        this(lineLength, CHUNK_SEPARATOR);
    }

    public Base64(int lineLength, byte[] lineSeparator) {
        this(lineLength, lineSeparator, false);
    }

    public Base64(int lineLength, byte[] lineSeparator, boolean urlSafe) {
        super(3, 4, lineLength, lineSeparator == null ? 0 : lineSeparator.length);
        this.decodeTable = DECODE_TABLE;
        if (lineSeparator == null) {
            this.encodeSize = 4;
            this.lineSeparator = null;
        } else if (containsAlphabetOrPad(lineSeparator)) {
            throw new IllegalArgumentException("lineSeparator must not contain base64 characters: [" + StringUtils.newStringUtf8(lineSeparator) + PropertyAccessor.PROPERTY_KEY_SUFFIX);
        } else if (lineLength > 0) {
            this.encodeSize = 4 + lineSeparator.length;
            this.lineSeparator = new byte[lineSeparator.length];
            System.arraycopy(lineSeparator, 0, this.lineSeparator, 0, lineSeparator.length);
        } else {
            this.encodeSize = 4;
            this.lineSeparator = null;
        }
        this.decodeSize = this.encodeSize - 1;
        this.encodeTable = urlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
    }

    public boolean isUrlSafe() {
        return this.encodeTable == URL_SAFE_ENCODE_TABLE;
    }

    @Override
    public void encode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
        if (!context.eof) {
            if (inAvail < 0) {
                context.eof = true;
                if (!(0 == context.modulus && this.lineLength == 0)) {
                    byte[] buffer = ensureBufferSize(this.encodeSize, context);
                    int savedPos = context.pos;
                    switch (context.modulus) {
                        case 0:
                            break;
                        case 1:
                            int i = context.pos;
                            context.pos = i + 1;
                            buffer[i] = this.encodeTable[(context.ibitWorkArea >> 2) & MASK_6BITS];
                            int i2 = context.pos;
                            context.pos = i2 + 1;
                            buffer[i2] = this.encodeTable[(context.ibitWorkArea << 4) & MASK_6BITS];
                            if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                                int i3 = context.pos;
                                context.pos = i3 + 1;
                                buffer[i3] = 61;
                                int i4 = context.pos;
                                context.pos = i4 + 1;
                                buffer[i4] = 61;
                                break;
                            }
                            break;
                        case 2:
                            int i5 = context.pos;
                            context.pos = i5 + 1;
                            buffer[i5] = this.encodeTable[(context.ibitWorkArea >> 10) & MASK_6BITS];
                            int i6 = context.pos;
                            context.pos = i6 + 1;
                            buffer[i6] = this.encodeTable[(context.ibitWorkArea >> 4) & MASK_6BITS];
                            int i7 = context.pos;
                            context.pos = i7 + 1;
                            buffer[i7] = this.encodeTable[(context.ibitWorkArea << 2) & MASK_6BITS];
                            if (this.encodeTable == STANDARD_ENCODE_TABLE) {
                                int i8 = context.pos;
                                context.pos = i8 + 1;
                                buffer[i8] = 61;
                                break;
                            }
                            break;
                        default:
                            throw new IllegalStateException("Impossible modulus " + context.modulus);
                    }
                    context.currentLinePos += context.pos - savedPos;
                    if (this.lineLength > 0 && context.currentLinePos > 0) {
                        System.arraycopy(this.lineSeparator, 0, buffer, context.pos, this.lineSeparator.length);
                        context.pos += this.lineSeparator.length;
                        return;
                    }
                    return;
                }
                return;
            }
            for (int i9 = 0; i9 < inAvail; i9++) {
                byte[] buffer2 = ensureBufferSize(this.encodeSize, context);
                context.modulus = (context.modulus + 1) % 3;
                inPos++;
                int b = in[inPos];
                if (b < 0) {
                    b += 256;
                }
                context.ibitWorkArea = (context.ibitWorkArea << 8) + b;
                if (0 == context.modulus) {
                    int i10 = context.pos;
                    context.pos = i10 + 1;
                    buffer2[i10] = this.encodeTable[(context.ibitWorkArea >> 18) & MASK_6BITS];
                    int i11 = context.pos;
                    context.pos = i11 + 1;
                    buffer2[i11] = this.encodeTable[(context.ibitWorkArea >> 12) & MASK_6BITS];
                    int i12 = context.pos;
                    context.pos = i12 + 1;
                    buffer2[i12] = this.encodeTable[(context.ibitWorkArea >> 6) & MASK_6BITS];
                    int i13 = context.pos;
                    context.pos = i13 + 1;
                    buffer2[i13] = this.encodeTable[context.ibitWorkArea & MASK_6BITS];
                    context.currentLinePos += 4;
                    if (this.lineLength > 0 && this.lineLength <= context.currentLinePos) {
                        System.arraycopy(this.lineSeparator, 0, buffer2, context.pos, this.lineSeparator.length);
                        context.pos += this.lineSeparator.length;
                        context.currentLinePos = 0;
                    }
                }
            }
        }
    }

    @Override
    public void decode(byte[] in, int inPos, int inAvail, BaseNCodec.Context context) {
        byte b;
        if (!context.eof) {
            if (inAvail < 0) {
                context.eof = true;
            }
            int i = 0;
            while (true) {
                if (i >= inAvail) {
                    break;
                }
                byte[] buffer = ensureBufferSize(this.decodeSize, context);
                inPos++;
                byte b2 = in[inPos];
                if (b2 == 61) {
                    context.eof = true;
                    break;
                }
                if (b2 >= 0 && b2 < DECODE_TABLE.length && (b = DECODE_TABLE[b2]) >= 0) {
                    context.modulus = (context.modulus + 1) % 4;
                    context.ibitWorkArea = (context.ibitWorkArea << 6) + b;
                    if (context.modulus == 0) {
                        int i2 = context.pos;
                        context.pos = i2 + 1;
                        buffer[i2] = (byte) ((context.ibitWorkArea >> 16) & Validators.CHARACTER_MAX_LENGTH);
                        int i3 = context.pos;
                        context.pos = i3 + 1;
                        buffer[i3] = (byte) ((context.ibitWorkArea >> 8) & Validators.CHARACTER_MAX_LENGTH);
                        int i4 = context.pos;
                        context.pos = i4 + 1;
                        buffer[i4] = (byte) (context.ibitWorkArea & Validators.CHARACTER_MAX_LENGTH);
                    }
                }
                i++;
            }
            if (context.eof && context.modulus != 0) {
                byte[] buffer2 = ensureBufferSize(this.decodeSize, context);
                switch (context.modulus) {
                    case 1:
                        return;
                    case 2:
                        context.ibitWorkArea >>= 4;
                        int i5 = context.pos;
                        context.pos = i5 + 1;
                        buffer2[i5] = (byte) (context.ibitWorkArea & Validators.CHARACTER_MAX_LENGTH);
                        return;
                    case 3:
                        context.ibitWorkArea >>= 2;
                        int i6 = context.pos;
                        context.pos = i6 + 1;
                        buffer2[i6] = (byte) ((context.ibitWorkArea >> 8) & Validators.CHARACTER_MAX_LENGTH);
                        int i7 = context.pos;
                        context.pos = i7 + 1;
                        buffer2[i7] = (byte) (context.ibitWorkArea & Validators.CHARACTER_MAX_LENGTH);
                        return;
                    default:
                        throw new IllegalStateException("Impossible modulus " + context.modulus);
                }
            }
        }
    }

    @Deprecated
    public static boolean isArrayByteBase64(byte[] arrayOctet) {
        return isBase64(arrayOctet);
    }

    public static boolean isBase64(byte octet) {
        return octet == 61 || (octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1);
    }

    public static boolean isBase64(String base64) {
        return isBase64(StringUtils.getBytesUtf8(base64));
    }

    public static boolean isBase64(byte[] arrayOctet) {
        for (int i = 0; i < arrayOctet.length; i++) {
            if (!(isBase64(arrayOctet[i]) || isWhiteSpace(arrayOctet[i]))) {
                return false;
            }
        }
        return true;
    }

    public static byte[] encodeBase64(byte[] binaryData) {
        return encodeBase64(binaryData, false);
    }

    public static String encodeBase64String(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase64(binaryData, false));
    }

    public static byte[] encodeBase64URLSafe(byte[] binaryData) {
        return encodeBase64(binaryData, false, true);
    }

    public static String encodeBase64URLSafeString(byte[] binaryData) {
        return StringUtils.newStringUtf8(encodeBase64(binaryData, false, true));
    }

    public static byte[] encodeBase64Chunked(byte[] binaryData) {
        return encodeBase64(binaryData, true);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked) {
        return encodeBase64(binaryData, isChunked, false);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe) {
        return encodeBase64(binaryData, isChunked, urlSafe, Integer.MAX_VALUE);
    }

    public static byte[] encodeBase64(byte[] binaryData, boolean isChunked, boolean urlSafe, int maxResultSize) {
        if (binaryData == null || binaryData.length == 0) {
            return binaryData;
        }
        Base64 b64 = isChunked ? new Base64(urlSafe) : new Base64(0, CHUNK_SEPARATOR, urlSafe);
        long len = b64.getEncodedLength(binaryData);
        if (len <= ((long) maxResultSize)) {
            return b64.encode(binaryData);
        }
        throw new IllegalArgumentException("Input array too big, the output array would be bigger (" + len + ") than the specified maximum size of " + maxResultSize);
    }

    public static byte[] decodeBase64(String base64String) {
        return new Base64().decode(base64String);
    }

    public static byte[] decodeBase64(byte[] base64Data) {
        return new Base64().decode(base64Data);
    }

    public static BigInteger decodeInteger(byte[] pArray) {
        return new BigInteger(1, decodeBase64(pArray));
    }

    public static byte[] encodeInteger(BigInteger bigInt) {
        if (bigInt != null) {
            return encodeBase64(toIntegerBytes(bigInt), false);
        }
        throw new NullPointerException("encodeInteger called with null parameter");
    }

    static byte[] toIntegerBytes(BigInteger bigInt) {
        int bitlen = ((bigInt.bitLength() + 7) >> 3) << 3;
        byte[] bigBytes = bigInt.toByteArray();
        if (bigInt.bitLength() % 8 != 0 && (bigInt.bitLength() / 8) + 1 == bitlen / 8) {
            return bigBytes;
        }
        int startSrc = 0;
        int len = bigBytes.length;
        if (bigInt.bitLength() % 8 == 0) {
            startSrc = 1;
            len--;
        }
        int startDst = (bitlen / 8) - len;
        byte[] resizedBytes = new byte[bitlen / 8];
        System.arraycopy(bigBytes, startSrc, resizedBytes, startDst, len);
        return resizedBytes;
    }

    @Override
    protected boolean isInAlphabet(byte octet) {
        return octet >= 0 && octet < this.decodeTable.length && this.decodeTable[octet] != -1;
    }
}
