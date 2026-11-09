package jp.kobe_u.cs27.allergydataservice.configuration.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * パスワードのハッシュ化・照合を行うユーティリティクラス
 * SHA-256 + ソルトによるハッシュ化を行う
 */
public class PasswordHasher {

  private static final String ALGORITHM = "SHA-256";
  private static final int SALT_LENGTH_BYTES = 16;
  private static final SecureRandom RANDOM = new SecureRandom();

  private PasswordHasher() {
  }

  /**
   * ランダムなソルトを生成する
   *
   * @return Base64エンコードされたソルト文字列
   */
  public static String generateSalt() {
    byte[] salt = new byte[SALT_LENGTH_BYTES];
    RANDOM.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  /**
   * パスワードをソルト付きでハッシュ化する
   *
   * @param rawPassword 平文パスワード
   * @param salt        Base64エンコードされたソルト
   * @return Base64エンコードされたハッシュ値
   */
  public static String hash(String rawPassword, String salt) {
    try {
      MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
      digest.update(Base64.getDecoder().decode(salt));
      byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hashed);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(ALGORITHM + " is not available", e);
    }
  }

  /**
   * パスワードが保存されているハッシュ値と一致するか確認する
   *
   * @param rawPassword  平文パスワード
   * @param salt         Base64エンコードされたソルト
   * @param expectedHash 保存されているハッシュ値
   * @return 一致すればtrue
   */
  public static boolean matches(String rawPassword, String salt, String expectedHash) {
    String actualHash = hash(rawPassword, salt);
    return MessageDigest.isEqual(
        actualHash.getBytes(StandardCharsets.UTF_8),
        expectedHash.getBytes(StandardCharsets.UTF_8));
  }

}
