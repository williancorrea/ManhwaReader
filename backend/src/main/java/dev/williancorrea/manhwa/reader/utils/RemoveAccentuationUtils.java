package dev.williancorrea.manhwa.reader.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RemoveAccentuationUtils {

  private static final String ACCENTUATION = "çÇáéíóúýÁÉÍÓÚÝàèìòùÀÈÌÒÙãõñäëïöüÿÄËÏÖÜÃÕÑâêîôûÂÊÎÔÛ";
  private static final String NO_ACCENTUATION = "cCaeiouyAEIOUYaeiouAEIOUaonaeiouyAEIOUAONaeiouAEIOU";
  private static final char[] table;

  static {
    table = new char[256];
    for (int i = 0; i < table.length; ++i) {
      table[i] = (char) i;
    }
    for (int i = 0; i < ACCENTUATION.length(); ++i) {
      table[ACCENTUATION.charAt(i)] = NO_ACCENTUATION.charAt(i);
    }
  }

  public static String removeAccentuation(final String word) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < word.length(); ++i) {
      char ch = word.charAt(i);
      if (ch < 256) {
        sb.append(table[ch]);
      } else {
        sb.append(ch);
      }
    }
    return sb.toString();
  }

  public static String normalize(final String word) {
    var text = removeAccentuation(word);
    text = text
        .trim()
        .replace(" ", "_")
        .replaceAll("[^a-zA-Z0-9_\\-.]", "")
    ;
    return text;
  }
}