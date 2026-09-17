package net.gommagomma.smfn.math.algebra.numerics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.gommagomma.smfn.math.algebra.structures.IntegerRing;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SignedInt: elemento (costruttore, Orderable, Absolutable, Exponentiable)")
class SignedIntTest {

    private final IntegerRing Z = IntegerRing.INSTANCE;

    private SignedInt si(long value) {
        return new SignedInt(value);
    }

    @Test
    @DisplayName("Costruttore e getter")
    void constructorAndGetter() {
        SignedInt twenty = si(20);
        assertEquals(20L, twenty.getValue());
        assertEquals("20", twenty.toString());
    }

    @Test
    @DisplayName("equals/hashCode/copy")
    void equalityAndCopy() {
        SignedInt a = si(100);
        SignedInt b = si(100);
        SignedInt c = si(101);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);

        SignedInt copy = a.copy();
        assertEquals(a, copy);
        assertNotSame(a, copy);
    }

    @Nested
    @DisplayName("Struttura: overflow additivo/moltiplicativo")
    class StructureOverflow {

        @Test
        void additiveOverflow() {
            assertThrows(ArithmeticException.class, () -> Z.add(si(Long.MAX_VALUE), si(1)));
        }

        @Test
        void negateLongMinValueOverflow() {
            assertThrows(ArithmeticException.class, () -> Z.negate(si(Long.MIN_VALUE)));
        }

        @Test
        void multiplicativeOverflow() {
            assertThrows(ArithmeticException.class, () -> Z.multiply(si(Long.MAX_VALUE), si(2)));
        }
    }

    @Nested
    @DisplayName("Absolutable")
    class AbsolutableTests {

        @Test
        void abs() {
            assertEquals(si(15), si(-15).abs());
            assertEquals(si(15), si(15).abs());
            assertEquals(si(0), si(0).abs());
        }

        @Test
        void absOverflow() {
            assertThrows(ArithmeticException.class, () -> si(Long.MIN_VALUE).abs());
        }

        @Test
        void signum() {
            assertEquals(1, si(15).signum());
            assertEquals(-1, si(-15).signum());
            assertEquals(0, si(0).signum());
        }
    }

    @Nested
    @DisplayName("Exponentiable")
    class PowerOperations {

        @ParameterizedTest(name = "{0}^{1} = {2}")
        @CsvSource({"5, 3, 125", "2, 10, 1024", "-2, 3, -8", "-3, 2, 9"})
        void positivePower(long base, int exp, long expected) {
            assertEquals(si(expected), si(base).power(exp));
        }

        @Test
        void powerZero() {
            assertEquals(si(1), si(500).power(0));
        }

        @Test
        void zeroPower() {
            assertEquals(si(0), si(0).power(5));
            assertEquals(si(1), si(0).power(0)); // 0^0 = 1 per convenzione
        }

        @Test
        void negativePowerThrowsException() {
            assertThrows(ArithmeticException.class, () -> si(5).power(-1));
        }

        @Test
        void powerOverflow() {
            assertThrows(ArithmeticException.class, () -> si(Long.MAX_VALUE).power(2));
        }
    }

    @Nested
    @DisplayName("Struttura: divisione Euclidea")
    class EuclideanDivision {

        @ParameterizedTest(name = "{0} / {1} = {2} (quotient)")
        @CsvSource({
            "10, 3, 3",
            "10, -3, -3",
            "-10, 3, -4",
            "-10, -3, 4"
        })
        void quotient(long dividend, long divisor, long expected) {
            assertEquals(si(expected), Z.quotient(si(dividend), si(divisor)));
        }

        @Test
        void quotientDivisionByZero() {
            assertThrows(ArithmeticException.class, () -> Z.quotient(si(10), Z.zero()));
        }

        @ParameterizedTest(name = "{0} % {1} = {2} (remainder normalizzato)")
        @CsvSource({
            "10, 3, 1",
            "10, -3, 1",
            "-10, 3, 2",
            "-10, -3, 2"
        })
        void remainderNormalised(long dividend, long divisor, long expectedRemainder) {
            SignedInt r = Z.remainder(si(dividend), si(divisor));
            assertEquals(si(expectedRemainder), r);
            assertTrue(r.getValue() >= 0, "Il resto deve essere >= 0");

            SignedInt q = Z.quotient(si(dividend), si(divisor));
            SignedInt check = Z.add(Z.multiply(q, si(divisor)), r);
            assertEquals(si(dividend), check, "La verifica a = q*b + r deve essere soddisfatta.");
        }

        @Test
        void remainderDivisionByZero() {
            assertThrows(ArithmeticException.class, () -> Z.remainder(si(10), Z.zero()));
        }
    }

    @Test
    @DisplayName("Orderable")
    void compareTo() {
        SignedInt a = si(10);
        SignedInt b = si(20);

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(si(10)));
        assertTrue(a.isLessThan(b));
    }

    @Nested
    @DisplayName("Struttura: creazione da double")
    class CreatableFromDoubleTests {

        @ParameterizedTest(name = "of({0}) -> {1}")
        @CsvSource({"11.0, 11", "-3.0, -3", "1.0, 1"})
        void ofValid(double input, long expected) {
            assertEquals(si(expected), Z.of(input));
        }

        @Test
        void ofLongMinValue() {
            assertEquals(si(Long.MIN_VALUE), Z.of((double) Long.MIN_VALUE));
        }

        @Test
        void ofLongMaxValue() {
            assertEquals(si(Long.MAX_VALUE), Z.of((double) Long.MAX_VALUE));
        }

        @Test
        @DisplayName("Valori non finiti lanciano IllegalArgumentException")
        void ofNonFiniteThrows() {
            assertThrows(IllegalArgumentException.class, () -> Z.of(Double.NaN));
            assertThrows(IllegalArgumentException.class, () -> Z.of(Double.POSITIVE_INFINITY));
            assertThrows(IllegalArgumentException.class, () -> Z.of(Double.NEGATIVE_INFINITY));
        }

        @Test
        @DisplayName("Valori non interi lanciano IllegalArgumentException")
        void ofNonIntegerThrows() {
            assertThrows(IllegalArgumentException.class, () -> Z.of(5.1));
        }

        @Test
        @DisplayName("Valori finiti oltre il range di long lanciano IllegalArgumentException (Math.round li ancora al bordo, la differenza supera epsilon)")
        void ofOutOfLongRangeThrows() {
            assertThrows(IllegalArgumentException.class, () -> Z.of(Double.MAX_VALUE));
            assertThrows(IllegalArgumentException.class, () -> Z.of(1.0e20));
        }

        @Test
        @DisplayName("Un valore minuscolo e' indistinguibile da zero entro epsilon: nessuna eccezione")
        void ofTinyValueRoundsToZero() {
            assertEquals(si(0), Z.of(Double.MIN_VALUE));
        }
    }
}
