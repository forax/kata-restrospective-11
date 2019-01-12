package fr.umlv.lexer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("static-method")
public class LexerTest {
  @Tag("Q1") @Test
  public void testEmpty() {
    assertTrue(Lexer.empty().tryParse("foo").isEmpty());
  }
  @Tag("Q1") @Test @SuppressWarnings("unused")
  public void testEmptyTyped() {
    Lexer<String> emptyString = Lexer.empty();
    Lexer<Integer> emptyInteger = Lexer.empty();
  }
  @Tag("Q1") @Test
  public void testEmptyInterned() {
    assertSame(Lexer.empty(), Lexer.empty());
  }
  @Tag("Q1") @Test @SuppressWarnings("unused")
  public void testEmptyTryParseTyped() {
    Optional<String> tokenString = Lexer.<String>empty().tryParse("foo");
    Optional<Integer> tokenInteger = Lexer.<Integer>empty().tryParse("123");
  }
  @Tag("Q1") @Test
  public void testEmptyTryParseNull() {
    assertThrows(NullPointerException.class, () -> Lexer.empty().tryParse(null));
  }
  
  /*
  interface LexerFactory {
    Lexer<String> create(String regex);
  }
  @SuppressWarnings("unused")
  private static Stream<LexerFactory> lexerFactories() {
    return Stream.of(
      text -> Lexer.from(Pattern.compile(text)), Lexer::from //Q6 , text -> Lexer.from(List.of(text), List.of(x -> x))
    );
  }
  
  @Tag("Q2") @ParameterizedTest @MethodSource("lexerFactories")
  public void testFromPatternRecognized(LexerFactory factory) {
    var lexer = factory.create("(ab*)");
    assertAll(
        () -> assertEquals("a", lexer.tryParse("a").orElseThrow()),
        () -> assertEquals("ab", lexer.tryParse("ab").orElseThrow()),
        () -> assertEquals("abb", lexer.tryParse("abb").orElseThrow()),
        () -> assertEquals("abbb", lexer.tryParse("abbb").orElseThrow()),
        () -> assertEquals("abbbb", lexer.tryParse("abbbb").orElseThrow())
        );
  }
  @Tag("Q2") @ParameterizedTest @MethodSource("lexerFactories")
  public void testFromPatternRecognizedSubmatch(LexerFactory factory) {
    var lexer = factory.create("([0-9]+)\\.[0-9]*");
    assertAll(
        () -> assertEquals("12", lexer.tryParse("12.6").orElseThrow()),
        () -> assertEquals("67", lexer.tryParse("67.").orElseThrow()),
        () -> assertEquals("63", lexer.tryParse("63.5").orElseThrow()),
        () -> assertEquals("45", lexer.tryParse("45.9").orElseThrow()),
        () -> assertEquals("0", lexer.tryParse("0.0").orElseThrow())
        );
  }
  @Tag("Q2") @ParameterizedTest @MethodSource("lexerFactories")
  public void testFromPatternUnrecognized(LexerFactory factory) {
    var lexer = factory.create("(ab*)");
    assertAll(
        () -> assertTrue(lexer.tryParse("").isEmpty()),
        () -> assertTrue(lexer.tryParse("b").isEmpty()),
        () -> assertTrue(lexer.tryParse("foo").isEmpty()),
        () -> assertTrue(lexer.tryParse("bar").isEmpty()),
        () -> assertTrue(lexer.tryParse("ba").isEmpty())
        );
  }
  @Tag("Q2") @ParameterizedTest @MethodSource("lexerFactories")
  public void testFromOnlyOneCaptureGroup(LexerFactory factory) {
    assertAll(
      () -> assertThrows(IllegalArgumentException.class, () -> factory.create("foo")),
      () -> assertThrows(IllegalArgumentException.class, () -> factory.create("(foo)(bar)"))
      );
  }
  @Tag("Q2") @ParameterizedTest @MethodSource("lexerFactories")
  public void testFromTryParseNull(LexerFactory factory) {
    assertThrows(NullPointerException.class, () -> factory.create("(foo)").tryParse(null));
  }
  @Tag("Q2") @Test
  public void testFromNull() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> Lexer.from((Pattern)null)),
        () -> assertThrows(NullPointerException.class, () -> Lexer.from((String)null))
        );
  }

  
  @Tag("Q3") @Test
  public void testMapRecognized() {
    assertAll(
      () -> assertEquals(42, (int)Lexer.from("([0-9]+)").map(Integer::parseInt).tryParse("42").orElseThrow()),
      () -> assertEquals(42.0, (double)Lexer.from("([0-9]+\\.[0-9]+)").map(Double::parseDouble).tryParse("42.0").orElseThrow())
      );
  }
  @Tag("Q3") @Test
  public void testMapUnrecognized() {
    assertAll(
      () -> assertTrue(Lexer.from("([0-9]+)").map(Integer::parseInt).tryParse("foo").isEmpty()),
      () -> assertTrue(Lexer.from("([0-9]+\\.[0-9]+)").map(Double::parseDouble).tryParse("bar").isEmpty()),
      () -> assertTrue(Lexer.<String>empty().map(Integer::parseInt).tryParse("foo").isEmpty()),
      () -> assertTrue(Lexer.<String>empty().map(Double::parseDouble).tryParse("bar").isEmpty())
      );
  }
  @Tag("Q3") @Test
  public void testMapNull() {
    assertThrows(NullPointerException.class, () -> Lexer.from("(f)oo").map(null));
  }
  @Tag("Q3") @Test
  public void testMapReturnNull() {
    assertTrue(Lexer.from("(foo)").map(__ -> null).tryParse("foo").isEmpty());
  }
  @Tag("Q3") @Test
  public void testMapSignature() {
    var lexer = Lexer.from("([0-9]+)").map(Integer::parseInt);
    assertEquals("1111", lexer.map((Object o) -> o.toString()).tryParse("1111").orElseThrow());
  }
  @Tag("Q3") @Test 
  public void testMapSignature2() {
    Lexer<Object> lexer = Lexer.from("([0-9]+)").map(Integer::parseInt);
    assertEquals(747, (int)lexer.tryParse("747").orElseThrow());
  }
  
  
  @Tag("Q4") @Test
  public void testOr() {
    var lexer = Lexer.from("([0-9]+)").or(Lexer.from("([a-z_]+)"));
    assertAll(
        () -> assertEquals("17", lexer.tryParse("17").orElseThrow()),
        () -> assertEquals("foo", lexer.tryParse("foo").orElseThrow()),
        () -> assertTrue(lexer.tryParse("$bar").isEmpty())
        );
  }
  @Tag("Q4") @Test
  public void testOrEmpty() {
    var lexer = Lexer.empty().or(Lexer.empty());
    assertAll(
        () -> assertTrue(lexer.tryParse("42").isEmpty()),
        () -> assertTrue(lexer.tryParse("foo").isEmpty()),
        () -> assertTrue(lexer.tryParse("_bar_").isEmpty())
        );
  }
  @Tag("Q4") @Test
  public void testOrWithMapRecognized() {
    var lexer = Lexer.from("([0-9]+)").<Object>map(Integer::parseInt).or(Lexer.from("([a-z_]+)"));
    assertAll(
        () -> assertEquals(17, lexer.tryParse("17").orElseThrow()),
        () -> assertEquals("foo", lexer.tryParse("foo").orElseThrow())
        );
  }
  @Tag("Q4") @Test
  public void testOrChooseFirst() {
  var lexer = Lexer.from("(goto)").map(__ -> 0).or(Lexer.from("([a-z]+)").map(__ -> 1));
  assertAll(
      () -> assertEquals(0, (int)lexer.tryParse("goto").orElseThrow()),
      () -> assertEquals(1, (int)lexer.tryParse("foo").orElseThrow()),
      () -> assertTrue(lexer.tryParse("42").isEmpty())
      );
  }
  @Tag("Q4") @Test
  public void testOrNull() {
    assertThrows(NullPointerException.class, () -> Lexer.from("(f)oo").or(null));
  }

  
  @Tag("Q5") @Test
  public void testWith() {
    var lexer = Lexer.<Integer>empty().with("(9)X?X?", Integer::parseInt);
    assertAll(
        () -> assertEquals(9, (int)lexer.tryParse("9").orElseThrow()),
        () -> assertEquals(9, (int)lexer.tryParse("9X").orElseThrow()),
        () -> assertEquals(9, (int)lexer.tryParse("9XX").orElseThrow()),
        () -> assertTrue(lexer.tryParse("XXX").isEmpty())
        );
  }
  @Tag("Q5") @Test
  public void testSeveralWiths() {
    var lexer = Lexer.empty()
        .with("(9)X?X?", Integer::parseInt)
        .with("(7)X?X?", Double::parseDouble);
    assertAll(
        () -> assertEquals(7.0, (double)lexer.tryParse("7").orElseThrow()),
        () -> assertEquals(9, (int)lexer.tryParse("9X").orElseThrow()),
        () -> assertEquals(7.0, (double)lexer.tryParse("7XX").orElseThrow()),
        () -> assertTrue(lexer.tryParse("XXX").isEmpty())
        );
  }
  @Tag("Q5") @Test
  public void testWithOneCaptureGroup() {
    assertAll(
      () -> assertThrows(IllegalArgumentException.class, () -> Lexer.empty().with("bar", x -> x)),
      () -> assertThrows(IllegalArgumentException.class, () -> Lexer.empty().with("(foo)(bar)", x -> x))
      );
  }
  @Tag("Q5") @Test
  public void testWithSomeNulls() {
    assertAll(
      () -> assertThrows(NullPointerException.class, () -> Lexer.empty().with(null, x -> x)),
      () -> assertThrows(NullPointerException.class, () -> Lexer.empty().with("(foo)", null))
      );
  }
  @Tag("Q5") @Test
  public void testCreate() {
    var lexer = Lexer.create()
        .with("([0-9]+)",          Integer::parseInt)
        .with("([0-9]+\\.[0-9]*)", Double::parseDouble)
        .with("([a-zA-Z]+)",       Function.identity());
    assertAll(
        () -> assertEquals("foo", lexer.tryParse("foo").orElseThrow()),
        () -> assertEquals(12.3, lexer.tryParse("12.3").orElseThrow()),
        () -> assertEquals(200, lexer.tryParse("200").orElseThrow()),
        () -> assertTrue(lexer.tryParse(".bar.").isEmpty())
        );
  }
  @Tag("Q5") @Test
  public void testCreateSubGroup() {
    var lexer = Lexer.create()
        .with("(9)X?X?", Integer::parseInt)
        .with("(7)X?X?", Double::parseDouble);
    assertAll(
        () -> assertEquals(7.0, lexer.tryParse("7").orElseThrow()),
        () -> assertEquals(9, lexer.tryParse("9X").orElseThrow()),
        () -> assertEquals(7.0, lexer.tryParse("7XX").orElseThrow()),
        () -> assertTrue(lexer.tryParse("XXX").isEmpty())
        );
  }
  @Tag("Q5") @Test
  public void testCreateOneWith() {
    var lexer = Lexer.<Integer>create()
        .with("(3)X?X?", Integer::parseInt);
    assertAll(
        () -> assertEquals(3, (int)lexer.tryParse("3").orElseThrow()),
        () -> assertTrue(lexer.tryParse("XXX").isEmpty())
        );
  }
  @Tag("Q5") @Test
  public void testCreateEmpty() {
    var lexer = Lexer.create();
    assertTrue(lexer.tryParse("foo").isEmpty());
  }
  

  @Tag("Q6") @Test
  public void testFromTwoLists() {
    var lexer = Lexer.from(
        List.of("([0-9]+)",        "([0-9]+\\.[0-9]*)"),
        List.of(Integer::parseInt, Double::parseDouble));
    assertAll(
        () -> assertEquals(123, (int)lexer.tryParse("123").orElseThrow()),
        () -> assertEquals(42.5, (double)lexer.tryParse("42.5").orElseThrow()),
        () -> assertTrue(lexer.tryParse("hello").isEmpty())
        );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsChooseFirst() {
    var lexer = Lexer.<Integer>from(
        List.of("(goto)", "([a-z]+)"),
        List.of(__ -> 0,  __ -> 1));
    assertAll(
        () -> assertEquals(0, (int)lexer.tryParse("goto").orElseThrow()),
        () -> assertEquals(1, (int)lexer.tryParse("foo").orElseThrow()),
        () -> assertTrue(lexer.tryParse("42").isEmpty())
        );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsEmpty() {
    var lexer = Lexer.from(List.of(), List.of());
    assertAll(
      () -> assertTrue(lexer.tryParse("42").isEmpty()),
      () -> assertTrue(lexer.tryParse("").isEmpty())
    );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsNonMutable() {
    var regexes = new ArrayList<String>();
    var functions = new ArrayList<UnaryOperator<String>>();
    var lexer = Lexer.<String>from(regexes, functions);
    regexes.add("(foo)");
    functions.add(x -> x);
    assertTrue(lexer.tryParse("foo").isEmpty());
  }
  @Tag("Q6") @Test
  public void testFromTwoListsMap() {
    var lexer = Lexer.<Integer>from(List.of("(break)", "([a-z]+)"), List.of(__ -> 0,  __ -> 1))
        .map(x -> x * 2);
    assertAll(
        () -> assertEquals(0, (int)lexer.tryParse("break").orElseThrow()),
        () -> assertEquals(2, (int)lexer.tryParse("foo").orElseThrow()),
        () -> assertTrue(lexer.tryParse("42").isEmpty())
        );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsOr() {
    var lexer = Lexer.from(List.of("(short)"), List.of(__ -> 0))
                     .or(Lexer.from(List.of("([a-z]+)"), List.of(__ -> 1)));
    assertAll(
        () -> assertEquals(0, (int)lexer.tryParse("short").orElseThrow()),
        () -> assertEquals(1, (int)lexer.tryParse("foo").orElseThrow()),
        () -> assertTrue(lexer.tryParse("42").isEmpty())
        );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsOrSimplerFrom() {
    var lexer = Lexer.from(List.of("(try)"), List.of(__ -> 0))
                     .or(Lexer.from("([a-z]+)").map(__ -> 1));
    assertAll(
        () -> assertEquals(0, (int)lexer.tryParse("try").orElseThrow()),
        () -> assertEquals(1, (int)lexer.tryParse("foo").orElseThrow()),
        () -> assertTrue(lexer.tryParse("42").isEmpty())
        );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsWith() {
    var lexer = Lexer.from(List.of("(try)"), List.of(__ -> 0))
                     .with("([a-z]+)", __ -> 1);
    assertAll(
        () -> assertEquals(0, (int)lexer.tryParse("try").orElseThrow()),
        () -> assertEquals(1, (int)lexer.tryParse("foo").orElseThrow()),
        () -> assertTrue(lexer.tryParse("42").isEmpty())
        );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsFunctionReturnNull() {
    var lexer = Lexer.from(
        List.of("(foo)"),
        List.of(__ -> null));
    assertTrue(lexer.tryParse("foo").isEmpty());
  }
  @Tag("Q6") @Test
  public void testFromTwoListsNotSameSize() {
    assertAll(
      () -> assertThrows(IllegalArgumentException.class, () -> Lexer.from(List.of("(foo)"), List.of())),
      () -> assertThrows(IllegalArgumentException.class, () -> Lexer.from(List.of(), List.of(x -> x)))
      );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsNotOnlyOneGroup() {
    assertAll(
      () -> assertThrows(IllegalArgumentException.class, () -> Lexer.from(List.of("(foo)", "bar"), List.of(x -> x, x -> x))),
      () -> assertThrows(IllegalArgumentException.class, () -> Lexer.from(List.of("(foo)", "(bar)(baz)"), List.of(x -> x, x -> x)))
      );
  }
  @Tag("Q6") @Test
  public void testFromTwoListsNull() {
    assertAll(
        () -> assertThrows(NullPointerException.class, () -> Lexer.from(null, List.of())),
        () -> assertThrows(NullPointerException.class, () -> Lexer.from(List.of(), null)),
        () -> assertThrows(NullPointerException.class, () -> Lexer.from(List.of((String)null), List.of())),
        () -> assertThrows(NullPointerException.class, () -> Lexer.from(List.of(), List.of((UnaryOperator<String>)null))),
        () -> assertThrows(NullPointerException.class, () -> Lexer.from(List.of("(foo)"), List.of(x ->x)).tryParse(null))
        );
  }
  
  
  @Tag("Q7") @Test
  public void testFromTwoListsOrAnotherFromTwoListsOptimization() {
    var lexer1 = Lexer.from(List.of("(finally)", "([a-z]+)"), List.of(__ -> 0,  __ -> 1));
    var lexer2 = Lexer.from(List.of("(123)", "([0-9]+)"), List.of(__ -> 2,  __ -> 3));
    var lexer3 = lexer1.or(lexer2);
    assertAll(
      () -> assertEquals(0, lexer3.tryParse("finally").orElseThrow()),
      () -> assertEquals(1, lexer3.tryParse("foo").orElseThrow()),
      () -> assertEquals(2, lexer3.tryParse("123").orElseThrow()),
      () -> assertEquals(3, lexer3.tryParse("456").orElseThrow()),
      () -> assertTrue(lexer3.tryParse("_bar_").isEmpty()),
      () -> assertSame(lexer1.getClass(), lexer2.getClass()),
      () -> assertSame(lexer1.getClass(), lexer3.getClass()),
      () -> assertSame(lexer2.getClass(), lexer3.getClass())
    );
  }
  
  
  @Tag("Q7") @Test
  public void testFromTwoListsWithOptimization() {
    var lexer1 = Lexer.from(List.of("(for)", "([a-z]+)"), List.of(__ -> 0,  __ -> 1));
    var lexer2 = lexer1.with("([0-9])+", __ -> 2);
    assertAll(
      () -> assertEquals(0, lexer2.tryParse("for").orElseThrow()),
      () -> assertEquals(1, lexer2.tryParse("bar").orElseThrow()),
      () -> assertEquals(2, lexer2.tryParse("456").orElseThrow()),
      () -> assertTrue(lexer2.tryParse("_bar_").isEmpty()),
      () -> assertSame(lexer1.getClass(), lexer2.getClass())
    );
  }
  @Tag("Q7") @Test
  public void testCreateWithOptimization() {
    var lexer1 = Lexer.from(
        List.of("([0-9]+)",        "([0-9]+\\.[0-9]*)", "([a-zA-Z]+)"),
        List.of(Integer::parseInt, Double::parseDouble, x -> x));
    var lexer2 = Lexer.create(conf -> conf
        .with("([0-9]+)",          Integer::parseInt)
        .with("([0-9]+\\.[0-9]*)", Double::parseDouble)
        .with("([a-zA-Z]+)",       x -> x));
    assertSame(lexer1.getClass(), lexer2.getClass());
  }*/
}
