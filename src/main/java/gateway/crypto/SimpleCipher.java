package gateway.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * XOR with a repeating key, then Base64. This is NOT real cryptography,
 * the task says a simple version is enough.
 */
public class SimpleCipher {

    private final byte[] key;

    public SimpleCipher(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("key must not be empty");
        }
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    public String encrypt(String text) {
        byte[] xored = xor(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(xored);
    }

    public String decrypt(String encoded) {
        byte[] raw = Base64.getDecoder().decode(encoded);
        return new String(xor(raw), StandardCharsets.UTF_8);
    }

    private byte[] xor(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ key[i % key.length]);
        }
        return result;
    }
}
