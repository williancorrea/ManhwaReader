package dev.williancorrea.manhwa.reader.scraper.mangotoons;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import dev.williancorrea.manhwa.reader.exception.custom.BusinessException;

public class MangoCrypto {

  private static final String SECRET = "abmPisXlFjOLVTnYhbYQTpkWJtOGKwVttzLqstfjRBNVaEtQYG";

  private static byte[] sha256(String input) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    return digest.digest(input.getBytes(StandardCharsets.UTF_8));
  }

  private static byte[] hexToBytes(String hex) {

    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
    }
    return data;
  }

  public static String decrypt(String encrypted) throws Exception {
    String[] parts = encrypted.split(":");
    if (parts.length != 2) {
      throw new BusinessException("scraper.mangotoons.error.invalid-payload", null);
    }

    byte[] iv = hexToBytes(parts[0]);
    byte[] cipherBytes = hexToBytes(parts[1]);

    byte[] key = sha256(SECRET + "salt");

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

    cipher.init(
        Cipher.DECRYPT_MODE,
        keySpec,
        new IvParameterSpec(iv)
    );

    byte[] decrypted = cipher.doFinal(cipherBytes);
    return new String(decrypted, StandardCharsets.UTF_8);
  }
}