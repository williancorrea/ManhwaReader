package dev.williancorrea.manhwa.reader.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StringUtils")
class StringUtilsTest {

  @Nested
  @DisplayName("constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should allow instantiation of StringUtils class")
    void testConstructor() {
      assertDoesNotThrow(StringUtils::new);
    }
  }

  @Nested
  @DisplayName("completeWithZeroZeroToRight")
  class CompleteWithZeroToRightTests {

    @Test
    @DisplayName("should append zeros to the right when string length is less than n")
    void testAppendZerosToRight() {
      String input = "123";
      int n = 5;
      String expected = "12300";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should append single zero")
    void testAppendSingleZero() {
      String input = "5";
      int n = 3;
      String expected = "500";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should append no zeros when string length equals n")
    void testNoZerosWhenLengthEquals() {
      String input = "12345";
      int n = 5;
      String expected = "12345";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should append no zeros when string length is greater than n")
    void testNoZerosWhenLengthGreater() {
      String input = "123456";
      int n = 5;
      String expected = "123456";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should trim leading and trailing spaces before appending zeros")
    void testTrimSpacesBeforeAppending() {
      String input = "  123  ";
      int n = 6;
      String expected = "123000";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should handle empty string")
    void testEmptyString() {
      String input = "";
      int n = 3;
      String expected = "000";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should handle string with only spaces")
    void testStringWithOnlySpaces() {
      String input = "   ";
      int n = 3;
      String expected = "000";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should handle large n value")
    void testLargeNValue() {
      String input = "1";
      int n = 10;
      String expected = "1000000000";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should handle zero n value")
    void testZeroNValue() {
      String input = "12345";
      int n = 0;
      String expected = "12345";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should handle negative n value")
    void testNegativeNValue() {
      String input = "123";
      int n = -5;
      String expected = "123";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should handle string with alphabetic characters")
    void testAlphabeticString() {
      String input = "abc";
      int n = 5;
      String expected = "abc00";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }

    @Test
    @DisplayName("should handle mixed alphanumeric string")
    void testMixedAlphanumericString() {
      String input = "a1b2";
      int n = 7;
      String expected = "a1b2000";
      assertEquals(expected, StringUtils.completeWithZeroZeroToRight(input, n));
    }
  }

  @Nested
  @DisplayName("completeWithZeroZeroToLeft")
  class CompleteWithZeroToLeftTests {

    @Test
    @DisplayName("should prepend zeros to the left when string length is less than n")
    void testPrependZerosToLeft() {
      String input = "123";
      int n = 5;
      String expected = "00123";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should prepend single zero")
    void testPrependSingleZero() {
      String input = "5";
      int n = 3;
      String expected = "005";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should prepend no zeros when string length equals n")
    void testNoZerosWhenLengthEquals() {
      String input = "12345";
      int n = 5;
      String expected = "12345";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should prepend no zeros when string length is greater than n")
    void testNoZerosWhenLengthGreater() {
      String input = "123456";
      int n = 5;
      String expected = "123456";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should trim leading and trailing spaces before prepending zeros")
    void testTrimSpacesBeforePrepending() {
      String input = "  123  ";
      int n = 6;
      String expected = "000123";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle empty string")
    void testEmptyString() {
      String input = "";
      int n = 3;
      String expected = "000";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle string with only spaces")
    void testStringWithOnlySpaces() {
      String input = "   ";
      int n = 3;
      String expected = "000";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle large n value")
    void testLargeNValue() {
      String input = "1";
      int n = 10;
      String expected = "0000000001";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle zero n value")
    void testZeroNValue() {
      String input = "12345";
      int n = 0;
      String expected = "12345";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle negative n value")
    void testNegativeNValue() {
      String input = "123";
      int n = -5;
      String expected = "123";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle string with alphabetic characters")
    void testAlphabeticString() {
      String input = "abc";
      int n = 5;
      String expected = "00abc";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle mixed alphanumeric string")
    void testMixedAlphanumericString() {
      String input = "a1b2";
      int n = 7;
      String expected = "000a1b2";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle real-world use case: zero-padded chapter numbers")
    void testRealWorldChapterNumbering() {
      String input = "5";
      int n = 4;
      String expected = "0005";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }

    @Test
    @DisplayName("should handle multiple digit padding")
    void testMultipleDigitPadding() {
      String input = "42";
      int n = 6;
      String expected = "000042";
      assertEquals(expected, StringUtils.completeWithZeroZeroToLeft(input, n));
    }
  }

  @Nested
  @DisplayName("completeWithZero (indirect testing through public methods)")
  class CompleteWithZeroTests {

    @Test
    @DisplayName("should calculate correct number of zeros needed")
    void testZeroCalculationViaRight() {
      String input = "1";
      int n = 5;
      String result = StringUtils.completeWithZeroZeroToRight(input, n);
      assertEquals("10000", result);
      assertEquals(5, result.length());
    }

    @Test
    @DisplayName("should calculate correct number of zeros needed via left")
    void testZeroCalculationViaLeft() {
      String input = "1";
      int n = 5;
      String result = StringUtils.completeWithZeroZeroToLeft(input, n);
      assertEquals("00001", result);
      assertEquals(5, result.length());
    }

    @Test
    @DisplayName("should use Math.max(0, fim) to avoid negative repetition")
    void testMathMaxHandlesNegative() {
      String input = "verylongstring";
      int n = 5;
      String resultRight = StringUtils.completeWithZeroZeroToRight(input, n);
      String resultLeft = StringUtils.completeWithZeroZeroToLeft(input, n);
      assertEquals("verylongstring", resultRight);
      assertEquals("verylongstring", resultLeft);
    }

    @Test
    @DisplayName("should properly trim before calculating zeros")
    void testTrimmingBeforeCalculation() {
      String input = "  12  ";
      int n = 5;
      String resultRight = StringUtils.completeWithZeroZeroToRight(input, n);
      String resultLeft = StringUtils.completeWithZeroZeroToLeft(input, n);
      assertEquals("12000", resultRight);
      assertEquals("00012", resultLeft);
      assertEquals(5, resultRight.length());
      assertEquals(5, resultLeft.length());
    }

    @Test
    @DisplayName("should handle zero padding for both directions consistently")
    void testConsistencyBetweenDirections() {
      String input = "42";
      int n = 5;
      String right = StringUtils.completeWithZeroZeroToRight(input, n);
      String left = StringUtils.completeWithZeroZeroToLeft(input, n);
      assertEquals(n, right.length());
      assertEquals(n, left.length());
      assertTrue(right.startsWith("42"));
      assertTrue(left.endsWith("42"));
    }
  }
}
