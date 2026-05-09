package dev.williancorrea.manhwa.reader.features.work.synchronization;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("SynchronizationOriginType")
class SynchronizationOriginTypeTest {

  @Nested
  @DisplayName("enum values")
  class EnumValuesTests {

    @Test
    @DisplayName("should have MANGADEX value")
    void shouldHaveMangadexValue() {
      assertThat(SynchronizationOriginType.MANGADEX).isNotNull();
      assertThat(SynchronizationOriginType.MANGADEX.name()).isEqualTo("MANGADEX");
    }

    @Test
    @DisplayName("should have LYCANTOONS value")
    void shouldHaveLycatoonsValue() {
      assertThat(SynchronizationOriginType.LYCANTOONS).isNotNull();
      assertThat(SynchronizationOriginType.LYCANTOONS.name()).isEqualTo("LYCANTOONS");
    }

    @Test
    @DisplayName("should have MANGOTOONS value")
    void shouldHaveMangoToonsValue() {
      assertThat(SynchronizationOriginType.MANGOTOONS).isNotNull();
      assertThat(SynchronizationOriginType.MANGOTOONS.name()).isEqualTo("MANGOTOONS");
    }

    @Test
    @DisplayName("should have MEDIOCRESCAN value")
    void shouldHaveMediocrescanValue() {
      assertThat(SynchronizationOriginType.MEDIOCRESCAN).isNotNull();
      assertThat(SynchronizationOriginType.MEDIOCRESCAN.name()).isEqualTo("MEDIOCRESCAN");
    }

    @Test
    @DisplayName("should have four values")
    void shouldHaveFourValues() {
      var values = SynchronizationOriginType.values();
      assertThat(values).hasSize(4);
    }
  }

  @Nested
  @DisplayName("valueOf()")
  class ValueOfTests {

    @Test
    @DisplayName("should return MANGADEX when given 'MANGADEX'")
    void shouldReturnMangadexWhenGivenMangadex() {
      var result = SynchronizationOriginType.valueOf("MANGADEX");
      assertThat(result).isEqualTo(SynchronizationOriginType.MANGADEX);
    }

    @Test
    @DisplayName("should return LYCANTOONS when given 'LYCANTOONS'")
    void shouldReturnLycatoonsWhenGivenLycatoons() {
      var result = SynchronizationOriginType.valueOf("LYCANTOONS");
      assertThat(result).isEqualTo(SynchronizationOriginType.LYCANTOONS);
    }

    @Test
    @DisplayName("should return MANGOTOONS when given 'MANGOTOONS'")
    void shouldReturnMangoToonsWhenGivenMangotoons() {
      var result = SynchronizationOriginType.valueOf("MANGOTOONS");
      assertThat(result).isEqualTo(SynchronizationOriginType.MANGOTOONS);
    }

    @Test
    @DisplayName("should return MEDIOCRESCAN when given 'MEDIOCRESCAN'")
    void shouldReturnMediocrescanWhenGivenMediocrescan() {
      var result = SynchronizationOriginType.valueOf("MEDIOCRESCAN");
      assertThat(result).isEqualTo(SynchronizationOriginType.MEDIOCRESCAN);
    }
  }

  @Nested
  @DisplayName("comparisons")
  class ComparisonTests {

    @Test
    @DisplayName("MANGADEX should equal itself")
    void mangadexShouldEqualItself() {
      assertThat(SynchronizationOriginType.MANGADEX)
          .isEqualTo(SynchronizationOriginType.MANGADEX);
    }

    @Test
    @DisplayName("MANGADEX should not equal LYCANTOONS")
    void mangadexShouldNotEqualLycatoons() {
      assertThat(SynchronizationOriginType.MANGADEX)
          .isNotEqualTo(SynchronizationOriginType.LYCANTOONS);
    }

    @Test
    @DisplayName("LYCANTOONS should not equal MANGOTOONS")
    void lycatoonsShouldNotEqualMangotoons() {
      assertThat(SynchronizationOriginType.LYCANTOONS)
          .isNotEqualTo(SynchronizationOriginType.MANGOTOONS);
    }

    @Test
    @DisplayName("different instances of same enum value should be equal")
    void differentInstancesOfSameEnumValueShouldBeEqual() {
      var value1 = SynchronizationOriginType.MANGADEX;
      var value2 = SynchronizationOriginType.valueOf("MANGADEX");

      assertThat(value1).isEqualTo(value2);
      assertThat(value1.hashCode()).isEqualTo(value2.hashCode());
    }
  }

  @Nested
  @DisplayName("use in conditionals")
  class UseInConditionalsTests {

    @Test
    @DisplayName("should work in equals comparison")
    void shouldWorkInEqualsComparison() {
      SynchronizationOriginType origin = SynchronizationOriginType.MANGADEX;

      boolean isMangaDex = origin == SynchronizationOriginType.MANGADEX;

      assertThat(isMangaDex).isTrue();
    }

    @Test
    @DisplayName("should work in switch statement")
    void shouldWorkInSwitchStatement() {
      SynchronizationOriginType origin = SynchronizationOriginType.LYCANTOONS;
      String result = "";

      switch (origin) {
        case MANGADEX -> result = "MangaDex";
        case LYCANTOONS -> result = "LycaToons";
        case MANGOTOONS -> result = "MangoToons";
        case MEDIOCRESCAN -> result = "Mediocrescan";
      }

      assertThat(result).isEqualTo("LycaToons");
    }

    @Test
    @DisplayName("should work in if-else")
    void shouldWorkInIfElse() {
      SynchronizationOriginType origin = SynchronizationOriginType.MEDIOCRESCAN;

      String result;
      if (origin == SynchronizationOriginType.MANGADEX) {
        result = "MangaDex";
      } else if (origin == SynchronizationOriginType.MEDIOCRESCAN) {
        result = "Mediocrescan";
      } else {
        result = "Other";
      }

      assertThat(result).isEqualTo("Mediocrescan");
    }
  }

  @Nested
  @DisplayName("ordinal values")
  class OrdinalValuesTests {

    @Test
    @DisplayName("MANGADEX should be first (ordinal 0)")
    void mangadexShouldBeFirst() {
      assertThat(SynchronizationOriginType.MANGADEX.ordinal()).isEqualTo(0);
    }

    @Test
    @DisplayName("LYCANTOONS should be second (ordinal 1)")
    void lycatoonsShouldBeSecond() {
      assertThat(SynchronizationOriginType.LYCANTOONS.ordinal()).isEqualTo(1);
    }

    @Test
    @DisplayName("MANGOTOONS should be third (ordinal 2)")
    void mangotoonsShouldBeThird() {
      assertThat(SynchronizationOriginType.MANGOTOONS.ordinal()).isEqualTo(2);
    }

    @Test
    @DisplayName("MEDIOCRESCAN should be fourth (ordinal 3)")
    void mediocrescanShouldBeFourth() {
      assertThat(SynchronizationOriginType.MEDIOCRESCAN.ordinal()).isEqualTo(3);
    }
  }
}
