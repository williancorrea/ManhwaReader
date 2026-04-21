package dev.williancorrea.manhwa.reader.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RemoveAccentuationUtils")
class RemoveAccentuationUtilsTest {

  @Nested
  @DisplayName("removeAccentuation")
  class RemoveAccentuationTests {

    @Test
    @DisplayName("should return string without accentuation when given accentuated characters")
    void testRemoveCommonAccentuation() {
      String input = "Açúcar";
      String expected = "Acucar";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle lowercase accentuated characters")
    void testRemoveLowercaseAccentuation() {
      String input = "àèìòù";
      String expected = "aeiou";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle uppercase accentuated characters")
    void testRemoveUppercaseAccentuation() {
      String input = "ÀÈÌÒÙ";
      String expected = "AEIOU";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle cedilla characters (ç and Ç)")
    void testRemoveCedilla() {
      String input = "çãõÇÃÕ";
      String expected = "caoCAO";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle circumflex accentuated characters")
    void testRemoveCircumflexAccentuation() {
      String input = "âêîôûÂÊÎÔÛ";
      String expected = "aeiouAEIOU";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle diaeresis accentuated characters")
    void testRemoveDiaeresis() {
      String input = "äëïöüÿÄËÏÖÜ";
      String expected = "aeiouyAEIOU";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle tilde accentuated characters")
    void testRemoveTildeAccentuation() {
      String input = "ñÑ";
      String expected = "nN";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should return same string when no accentuation is present")
    void testReturnSameWhenNoAccentuation() {
      String input = "ABC123xyz";
      String expected = "ABC123xyz";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle empty string")
    void testEmptyString() {
      String input = "";
      String expected = "";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should preserve characters with code greater than 256")
    void testPreserveCharactersAbove256() {
      String input = "café™©";
      String expected = "cafe™©";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle mixed accentuated and non-accentuated characters")
    void testMixedAccentuation() {
      String input = "São Paulo - 2024";
      String expected = "Sao Paulo - 2024";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle special characters and numbers")
    void testSpecialCharactersAndNumbers() {
      String input = "Açúcar@123!#$%";
      String expected = "Acucar@123!#$%";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }

    @Test
    @DisplayName("should handle all accentuation map characters")
    void testAllAccentuationMapCharacters() {
      String accentuated = "çÇáéíóúýÁÉÍÓÚÝàèìòùÀÈÌÒÙãõñäëïöüÿÄËÏÖÜÃÕÑâêîôûÂÊÎÔÛ";
      String expected = "cCaeiouyAEIOUYaeiouAEIOUaonaeiouyAEIOUAONaeiouAEIOU";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(accentuated));
    }

    @Test
    @DisplayName("should handle spaces in the middle")
    void testSpacesInMiddle() {
      String input = "Café com Açúcar";
      String expected = "Cafe com Acucar";
      assertEquals(expected, RemoveAccentuationUtils.removeAccentuation(input));
    }
  }

  @Nested
  @DisplayName("normalize")
  class NormalizeTests {

    @Test
    @DisplayName("should normalize simple string with accentuation")
    void testNormalizeSimpleString() {
      String input = "Café";
      String expected = "Cafe";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should replace spaces with underscores")
    void testReplaceSpacesWithUnderscores() {
      String input = "São Paulo";
      String expected = "Sao_Paulo";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should handle multiple spaces")
    void testMultipleSpaces() {
      String input = "São   Paulo";
      String expected = "Sao___Paulo";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should trim leading and trailing whitespace")
    void testTrimWhitespace() {
      String input = "  Café  ";
      String expected = "Cafe";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should remove special characters except underscore and hyphen and dot")
    void testRemoveSpecialCharacters() {
      String input = "Café@#$%&*()";
      String expected = "Cafe";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should preserve numbers")
    void testPreserveNumbers() {
      String input = "Manga 123";
      String expected = "Manga_123";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should preserve hyphens")
    void testPreserveHyphens() {
      String input = "Manga-001";
      String expected = "Manga-001";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should preserve dots")
    void testPreserveDots() {
      String input = "Manga.v1.0";
      String expected = "Manga.v1.0";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should preserve underscores")
    void testPreserveUnderscores() {
      String input = "Manga_Chapter_001";
      String expected = "Manga_Chapter_001";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should handle complex string with all transformations")
    void testComplexString() {
      String input = "  Açúcar e Café 001-v2  ";
      String expected = "Acucar_e_Cafe_001-v2";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should handle empty string")
    void testEmptyString() {
      String input = "";
      String expected = "";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should handle string with only spaces")
    void testOnlySpaces() {
      String input = "   ";
      String expected = "";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should handle string with only special characters")
    void testOnlySpecialCharacters() {
      String input = "@#$%&*()";
      String expected = "";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should convert to lowercase when applying case-insensitive regex")
    void testUppercaseConversion() {
      String input = "CAFÉ";
      String expected = "CAFE";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
    }

    @Test
    @DisplayName("should handle mixed case with numbers and symbols")
    String testMixedCaseAndSymbols() {
      String input = "São Paulo @!# 2024";
      String expected = "Sao_Paulo_2024";
      assertEquals(expected, RemoveAccentuationUtils.normalize(input));
      return expected;
    }
  }
}
