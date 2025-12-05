package net.gommagomma.smfn.math.algebra.numeric;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Natural: Test delle proprietà di Semianello e Overflow")
class NaturalTest {

    // Helper per una creazione rapida e confronto
    private Natural n(long value) {
        return new Natural(value);
    }

    // ======================================================================================
    // COSTRUTTORE E PROPRIETÀ BASE
    // ======================================================================================

    @Test
    @DisplayName("Costruttore, Getter e Costanti")
    void constructorAndBasicProps() {
        assertEquals(0L, Natural.ZERO.getValue());
        assertEquals(1L, Natural.ONE.getValue());
        assertEquals(123L, n(123L).getValue());

        // Test IllegalArgumentException per valori negativi
        assertThrows(IllegalArgumentException.class, () -> 
            new Natural(-1), 
            "Il costruttore dovrebbe lanciare IllegalArgumentException per input negativo."
        );
    }

    @Test
    @DisplayName("Copia, Uguaglianza Matematica e Standard")
    void equalityAndCopy() {
        Natural a = n(50L);
        Natural b = n(50L);
        Natural c = n(51L);

        // Uguaglianza Standard (equals)
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(c));
        assertFalse(a.equals(null));
        assertFalse(a.equals(new Object()));

        // Uguaglianza Matematica
        assertTrue(a.isMathematicallyEqualTo(b));
        assertFalse(a.isMathematicallyEqualTo(c));
        
        // Copia
        Natural copy = a.copy();
        assertEquals(a, copy);
        assertNotSame(a, copy);
    }
    
    @Test
    @DisplayName("getZero, getOne")
    void identityElements() {
        assertEquals(Natural.ZERO, n(5).getZero());
        assertEquals(Natural.ONE, n(5).getOne());
    }

    // ======================================================================================
    // OPERAZIONI DI SEMIANELLO (Semiring Operations)
    // ======================================================================================

    @Nested
    @DisplayName("Addizione (Monoid Element)")
    class AdditiveOperations {
        
        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({"10, 5, 15", "0, 100, 100", "5, 0, 5", "1000, 2000, 3000"})
        void add(long a, long b, long expected) {
            assertEquals(n(expected), n(a).add(n(b)));
        }

        @Test
        @DisplayName("Overflow in Addizione")
        void addOverflow() {
            Natural max = n(Long.MAX_VALUE);
            Natural one = Natural.ONE;

            // Math.addExact lancia ArithmeticException se si verifica un overflow
            assertThrows(ArithmeticException.class, () -> 
                max.add(one), 
                "L'addizione dovrebbe lanciare ArithmeticException in caso di overflow."
            );
        }
    }

    @Nested
    @DisplayName("Moltiplicazione (Multiplicative Monoid Element)")
    class MultiplicativeOperations {
        
        @ParameterizedTest(name = "{0} * {1} = {2}")
        @CsvSource({"10, 5, 50", "1, 100, 100", "5, 0, 0", "10, 1, 10"})
        void multiply(long a, long b, long expected) {
            assertEquals(n(expected), n(a).multiply(n(b)));
        }

        @Test
        @DisplayName("Overflow in Moltiplicazione")
        void multiplyOverflow() {
            // 1. Definisci il limite: Long.MAX_VALUE / 2 + 1
            // La metà di Long.MAX_VALUE (9.22e18) è circa 4.61e18.
            
            // Scegliamo il valore più semplice che garantisca l'overflow quando moltiplicato per 2
            long halfMax = Long.MAX_VALUE / 2;
            Natural large = n(halfMax + 1); // Questo è il primo numero che causa overflow quando * 2
            Natural two = n(2L);

            // (halfMax + 1) * 2 = 2 * halfMax + 2, che è > Long.MAX_VALUE.
            assertThrows(ArithmeticException.class, () -> 
                large.multiply(two), 
                "La moltiplicazione dovrebbe lanciare ArithmeticException in caso di overflow."
            );
        }
    }

    // ======================================================================================
    // CAPACITÀ AGGIUNTIVE
    // ======================================================================================
    
    @Nested
    @DisplayName("Potenza (ExponentiableElement)")
    class PowerOperations {

        @ParameterizedTest(name = "{0}^{1} = {2}")
        @CsvSource({"2, 4, 16", "5, 3, 125", "10, 0, 1", "0, 5, 0", "1, 1000, 1"})
        void power(long base, int exp, long expected) {
            assertEquals(n(expected), n(base).power(exp));
        }

        @Test
        @DisplayName("Potenza Negativa (Eccezione)")
        void powerNegativeExponentThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                n(5).power(-1), 
                "La potenza negativa dovrebbe lanciare ArithmeticException."
            );
        }

        @Test
        @DisplayName("Overflow in Potenza")
        void powerOverflow() {
            Natural base = n(Long.MAX_VALUE / 2); 
            
            assertThrows(ArithmeticException.class, () -> 
                base.power(2), 
                "La potenza dovrebbe lanciare ArithmeticException in caso di overflow."
            );
            
            // Caso più piccolo (es. 9223372036854775807 / 2 = 4611686018427387903)
            assertThrows(ArithmeticException.class, () -> 
                n(3037000500L).power(2), // sqrt(MAX_VALUE) ~ 3.037e9
                "La potenza dovrebbe lanciare ArithmeticException in caso di overflow."
            );
        }
    }

    @Nested
    @DisplayName("Comparazione (ComparableElement)")
    class ComparisonTests {
        
        @Test
        void compareTo() {
            Natural small = n(100L);
            Natural large = n(200L);
            Natural equal = n(100L);
            
            assertTrue(small.compareTo(large) < 0);
            assertTrue(large.compareTo(small) > 0);
            assertEquals(0, small.compareTo(equal));
        }
    }

    @Nested
    @DisplayName("Creazione da Double (CreatableFromDouble)")
    class CreatableFromDoubleTests {

        @ParameterizedTest
        @ValueSource(doubles = {123.0, 99999.0, 0.0, 5.5, 123.99999})
        void valueOfValid(double input) {
            Natural result = n(0).valueOf(input); 
            // La conversione a long tronca la parte decimale
            assertEquals((long) input, result.getValue());
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.0, -0.0001, -123.45})
        void valueOfNegativeThrowsException(double input) {
            assertThrows(IllegalArgumentException.class, () -> 
                n(0).valueOf(input), 
                "valueOf dovrebbe lanciare IllegalArgumentException per valori negativi."
            );
        }
        
        @Test
        @DisplayName("valueOf Overflow")
        void valueOfOverflow() {
            // Testiamo un valore double maggiore di Long.MAX_VALUE
            // Math.pow(2, 63) che è il primo double > Long.MAX_VALUE
            double overflowValue = 9.223372036854776E18 * 2.0; 
            
            assertThrows(ArithmeticException.class, () -> 
                n(0).valueOf(overflowValue), 
                "valueOf dovrebbe lanciare ArithmeticException per overflow."
            );
        }
        
        @Test
        @DisplayName("valueOf Non Finiti")
        void valueOfNonFinite() {
            // NaN e Infinity non sono numeri Naturali validi
            assertThrows(IllegalArgumentException.class, () -> 
                n(0).valueOf(Double.NaN), 
                "valueOf dovrebbe lanciare IllegalArgumentException per NaN."
            );
            assertThrows(IllegalArgumentException.class, () -> 
                n(0).valueOf(Double.POSITIVE_INFINITY), 
                "valueOf dovrebbe lanciare IllegalArgumentException per Infinity."
            );
        }
    }
}