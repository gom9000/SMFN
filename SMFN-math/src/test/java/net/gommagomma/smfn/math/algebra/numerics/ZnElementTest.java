package net.gommagomma.smfn.math.algebra.numerics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import net.gommagomma.smfn.math.algebra.structures.ZnRing;

@DisplayName("ZnElement: elemento di Z/nZ (aritmetica modulare)")
class ZnElementTest {

    private static final SignedInt MOD_7 = new SignedInt(7);
    private static final SignedInt MOD_12 = new SignedInt(12);
    private final ZnRing Z7 = ZnRing.of(MOD_7);
    private final ZnRing Z12 = ZnRing.of(MOD_12);

    private ZnElement zn7(long value) {
        return new ZnElement(new SignedInt(value), MOD_7);
    }

    private ZnElement zn12(long value) {
        return new ZnElement(new SignedInt(value), MOD_12);
    }

    @Test
    @DisplayName("Costruttore: modulo deve essere positivo")
    void constructorInvalidModulus() {
        assertThrows(IllegalArgumentException.class, () ->
            new ZnElement(new SignedInt(5), new SignedInt(0)));
        assertThrows(IllegalArgumentException.class, () ->
            new ZnElement(new SignedInt(5), new SignedInt(-5)));
    }

    @Test
    @DisplayName("Costruttore: normalizzazione del valore")
    void constructorValueNormalization() {
        ZnElement tenMod7 = zn7(10); // 10 mod 7 = 3
        assertEquals(3L, tenMod7.getValue().getValue());
        assertEquals(MOD_7, tenMod7.getModulus());

        ZnElement minusOneMod7 = zn7(-1); // -1 mod 7 = 6
        assertEquals(6L, minusOneMod7.getValue().getValue());

        ZnElement sevenMod7 = zn7(7); // 7 mod 7 = 0
        assertEquals(0L, sevenMod7.getValue().getValue());
    }

    @Test
    @DisplayName("equals/hashCode")
    void equalityAndHashCode() {
        ZnElement a = zn7(10); // [3] mod 7
        ZnElement b = zn7(3);  // [3] mod 7
        ZnElement c = zn7(4);  // [4] mod 7
        ZnElement d = zn12(3); // [3] mod 12 (modulo diverso)

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(c));
        assertFalse(a.equals(d), "Elementi di anelli diversi non sono uguali.");
    }

    @Test
    @DisplayName("Copia")
    void copy() {
        ZnElement original = zn7(5);
        ZnElement copy = original.copy();
        assertEquals(original, copy);
        assertNotSame(original, copy);
    }

    @Test
    @DisplayName("toString")
    void toStringFormat() {
        assertEquals("[3 mod 7]", zn7(3).toString());
        assertEquals("[1 mod 12]", zn12(13).toString());
    }

    @Nested
    @DisplayName("Exponentiable")
    class PowerOperations {

        @ParameterizedTest(name = "[{0}]^{1} mod 7 = [{2}]")
        @CsvSource({"3, 2, 2", "2, 3, 1", "5, 0, 1"}) // 3^2=9=2, 2^3=8=1, x^0=1
        void power(long base, int exponent, long expected) {
            assertEquals(zn7(expected), zn7(base).power(exponent));
        }
    }

    @Nested
    @DisplayName("Struttura: operazioni modulari (via ZnRing)")
    class StructureOperations {

        @ParameterizedTest(name = "[{0}] + [{1}] = [{2}] mod 7")
        @CsvSource({
            "3, 2, 5",
            "5, 4, 2",
            "6, 1, 0",
            "6, 6, 5"
        })
        void add(long a, long b, long expected) {
            assertEquals(zn7(expected), Z7.add(zn7(a), zn7(b)));
        }

        @Test
        @DisplayName("Addizione tra elementi di anelli diversi lancia eccezione")
        void addDifferentModulus() {
            assertThrows(IllegalArgumentException.class, () -> Z7.add(zn7(5), zn12(5)));
        }

        @Test
        @DisplayName("Negazione modulare")
        void negate() {
            assertEquals(zn7(4), Z7.negate(zn7(3))); // -3 mod 7 = 4
            assertEquals(zn7(0), Z7.negate(zn7(0)));
            assertEquals(zn12(8), Z12.negate(zn12(4))); // -4 mod 12 = 8
        }

        @ParameterizedTest(name = "[{0}] * [{1}] = [{2}] mod 7")
        @CsvSource({
            "3, 2, 6",
            "4, 2, 1",
            "3, 3, 2"
        })
        void multiply(long a, long b, long expected) {
            assertEquals(zn7(expected), Z7.multiply(zn7(a), zn7(b)));
        }

        @Test
        @DisplayName("Moltiplicazione tra elementi di anelli diversi lancia eccezione")
        void multiplyDifferentModulus() {
            assertThrows(IllegalArgumentException.class, () -> Z7.multiply(zn7(5), zn12(5)));
        }

        @Test
        @DisplayName("Identita' moltiplicativa")
        void one() {
            assertEquals(zn7(1), Z7.one());
        }
    }

    @Test
    @DisplayName("zero()/isZero() coerenti")
    void zeroAndIsZero() {
        assertTrue(Z7.isZero(zn7(0)));
        assertTrue(Z7.isZero(zn7(7))); // [7] mod 7 = [0]
        assertFalse(Z7.isZero(zn7(1)));
    }
}
