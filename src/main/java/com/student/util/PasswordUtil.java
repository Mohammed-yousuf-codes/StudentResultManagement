package com.student.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Salted PBKDF2 password hashing. Stored format: pbkdf2$iterations$salt$hash (Base64). */
public final class PasswordUtil {
    private static final int ITERATIONS = 210_000;
    private static final SecureRandom RNG = new SecureRandom();

    private PasswordUtil() {}

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        byte[] h = pbkdf2(password, salt, ITERATIONS);
        Base64.Encoder enc = Base64.getEncoder();
        return "pbkdf2$" + ITERATIONS + "$" + enc.encodeToString(salt) + "$" + enc.encodeToString(h);
    }

    public static boolean verify(String password, String stored) {
        if (password == null || stored == null) return false;
        if (stored.startsWith("pbkdf2$")) {
            String[] p = stored.split("\\$");
            if (p.length != 4) return false;
            Base64.Decoder dec = Base64.getDecoder();
            byte[] expected = dec.decode(p[3]);
            byte[] actual = pbkdf2(password, dec.decode(p[2]), Integer.parseInt(p[1]));
            return MessageDigest.isEqual(expected, actual);
        }
        // Legacy formats from the original project: unsalted SHA-256 hex, or plain text.
        String candidate = stored.matches("[0-9a-f]{64}") ? sha256Hex(password) : password;
        return MessageDigest.isEqual(candidate.getBytes(StandardCharsets.UTF_8),
                                     stored.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean needsUpgrade(String stored) {
        return stored == null || !stored.startsWith("pbkdf2$" + ITERATIONS + "$");
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, 256);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("PBKDF2 unavailable", e);
        }
    }

    private static String sha256Hex(String s) {
        try {
            byte[] d = MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder b = new StringBuilder();
            for (byte x : d) b.append(String.format("%02x", x));
            return b.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /** Prints a hash for seeding: java -cp target/classes com.student.util.PasswordUtil secret */
    public static void main(String[] args) {
        System.out.println(hash(args.length > 0 ? args[0] : "admin123"));
    }
}
