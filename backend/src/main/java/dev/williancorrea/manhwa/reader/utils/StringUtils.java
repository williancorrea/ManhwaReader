package dev.williancorrea.manhwa.reader.utils;

public class StringUtils {

  /**
   * Appends a specified number of zeros to the right of the given string.
   *
   * <p>The method trims leading and trailing whitespace from the input string and then appends
   * a number of zeros determined by the {@code completeWithZero} method.
   *
   * @param value The input string to be modified.
   * @param n     The number of zeros to append.
   * @return A new string with the specified number of zeros appended to the right.
   */
  public static String completeWithZeroZeroToRight(String value, int n) {
    return value.trim() + completeWithZero(value, n);
  }

  /**
   * Prepends a specified number of zeros to the left of the given string.
   *
   * <p>The method trims leading and trailing whitespace from the input string and then prepends
   * a number of zeros determined by the {@code completeWithZero} method.
   *
   * @param value The input string to be modified.
   * @param n     The number of zeros to prepend.
   * @return A new string with the specified number of zeros added to the left.
   */
  public static String completeWithZeroZeroToLeft(String value, int n) {
    return completeWithZero(value, n) + value.trim();
  }

  /**
   * Generates a string of zeros to pad a given value to a specified length.
   *
   * @param value The input string to be padded. It will be trimmed before processing.
   * @param n     The desired total length after padding.
   * @return A StringBuilder containing a string of zeros. The number of zeros
   * is calculated as the difference between the desired length (n) and
   * the length of the trimmed input string. If this difference is
   * negative or zero, an empty StringBuilder is returned.
   */
  private static StringBuilder completeWithZero(String value, int n) {
    var s = value.trim();
    var resp = new StringBuilder();
    int fim = n - s.length();
    return resp.append("0".repeat(Math.max(0, fim)));
  }
}
