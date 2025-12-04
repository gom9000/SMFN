package net.gommagomma.smfn.math.algebra.numeric;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SignedInt: Test delle proprietà algebriche e di Dominio Euclideo")
class SignedIntTest {

    // Helper per una creazione rapida e confronto
    private SignedInt si(long value) {
        return new SignedInt(value);
    }

    // ======================================================================================
    // COSTRUTTORE E PROPRIETÀ BASE
    // ======================================================================================

    @Test
    @DisplayName("Costruttore e Getter")
    void constructorAndGetter() {
        SignedInt twenty = si(20);
        assertEquals(20L, twenty.getValue());
        assertEquals("20", twenty.toString());
        assertEquals(SignedInt.ZERO, si(0));
        assertEquals(SignedInt.ONE, si(1));
    }

    @Test
    @DisplayName("isMathematicallyEqualTo e equals/hashCode")
    void equalityAndHashCode() {
        SignedInt a = si(100);
        SignedInt b = si(100);
        SignedInt c = si(101);

        assertTrue(a.isMathematicallyEqualTo(b));
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        
        assertFalse(a.isMathematicallyEqualTo(c));
        assertFalse(a.equals(c));
        assertNotEquals(a.hashCode(), c.hashCode());
    }

    @Test
    @DisplayName("Copia e Identità")
    void copyAndIdentities() {
        SignedInt original = si(5);
        SignedInt copy = original.copy();

        assertEquals(original, copy);
        assertNotSame(original, copy);

        assertTrue(si(0).isZero());
        assertTrue(si(1).isOne());
        
        assertEquals(SignedInt.ZERO, si(0).getZero());
        assertEquals(SignedInt.ONE, si(1).getOne());
    }
    
    // ======================================================================================
    // OPERAZIONI DI ANELLO COMMUTATIVO (RING OPERATIONS)
    // ======================================================================================

    @Nested
    @DisplayName("Operazioni di Addizione e Sottrazione")
    class AdditiveOperations {
        
        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({"5, 3, 8", "-5, 3, -2", "10, -10, 0", "-1, -1, -2"})
        void add(long a, long b, long expected) {
            assertEquals(si(expected), si(a).add(si(b)));
        }

        @Test
        void subtract() {
            SignedInt a = si(10);
            SignedInt b = si(4);
            assertEquals(si(6), a.subtract(b)); // 10 - 4 = 6
        }

        @Test
        void negate() {
            assertEquals(si(-15), si(15).negate());
            assertEquals(si(15), si(-15).negate());
        }

        @Test
        void additiveOverflow() {
            assertThrows(ArithmeticException.class, () -> 
                si(Long.MAX_VALUE).add(si(1)), 
                "L'addizione dovrebbe causare overflow"
            );
        }

        @Test
        void negateLongMinValueOverflow() {
            assertThrows(ArithmeticException.class, () -> 
                si(Long.MIN_VALUE).negate(), 
                "La negazione di MIN_VALUE dovrebbe causare overflow"
            );
        }
    }

    @Nested
    @DisplayName("Operazioni di Moltiplicazione")
    class MultiplicativeOperations {
        
        @ParameterizedTest(name = "{0} * {1} = {2}")
        @CsvSource({"5, 3, 15", "-5, 3, -15", "10, 0, 0", "-1, -1, 1"})
        void multiply(long a, long b, long expected) {
            assertEquals(si(expected), si(a).multiply(si(b)));
        }
        
        @Test
        void multiplicativeOverflow() {
            assertThrows(ArithmeticException.class, () -> 
                si(Long.MAX_VALUE).multiply(si(2)), 
                "La moltiplicazione dovrebbe causare overflow"
            );
        }
    }

    // ======================================================================================
    // CAPACITÀ: EXPONENTIABLE
    // ======================================================================================

    @Nested
    @DisplayName("Potenza (ExponentiableElement)")
    class PowerOperations {
        
        @ParameterizedTest(name = "{0}^{1} = {2}")
        @CsvSource({"5, 3, 125", "2, 10, 1024", "-2, 3, -8", "-3, 2, 9"})
        void positivePower(long base, int exp, long expected) {
            assertEquals(si(expected), si(base).power(exp));
        }

        @Test
        void powerZero() {
            assertEquals(SignedInt.ONE, si(500).power(0));
        }

        @Test
        void zeroPower() {
            assertEquals(SignedInt.ZERO, si(0).power(5));
            assertEquals(SignedInt.ONE, si(0).power(0)); // 0^0 = 1 per convenzione
        }

        @Test
        void negativePowerThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                si(5).power(-1), 
                "La potenza negativa dovrebbe lanciare ArithmeticException"
            );
        }
        
        @Test
        void powerOverflow() {
            SignedInt max = si(Long.MAX_VALUE);
            assertThrows(ArithmeticException.class, () -> 
                max.power(2), 
                "La potenza dovrebbe causare overflow"
            );
        }
    }

    // ======================================================================================
    // CAPACITÀ: DOMINIO EUCLIDEO (EUCLIDEAN DOMAIN)
    // ======================================================================================

    @Nested
    @DisplayName("Divisione Euclidea")
    class EuclideanDivision {
        
        @Test
        void normValue() {
            assertEquals(si(5), si(5).normValue());
            assertEquals(si(5), si(-5).normValue());
            assertEquals(si(0), si(0).normValue());
        }

        @ParameterizedTest(name = "{0} / {1} = {2} (quotient)")
        @CsvSource({
            "10, 3, 3",     
            "10, -3, -3",   // AGGIORNATO da -4 a -3
            "-10, 3, -4",   
            "-10, -3, 4"    // AGGIORNATO da 3 a 4
        })
        void quotient(long dividend, long divisor, long expected) {
            assertEquals(si(expected), si(dividend).quotient(si(divisor)));
        }

        @Test
        void quotientDivisionByZero() {
            assertThrows(ArithmeticException.class, () -> 
                si(10).quotient(SignedInt.ZERO), 
                "La divisione per zero dovrebbe lanciare un'eccezione"
            );
        }

        @ParameterizedTest(name = "{0} % {1} = {2} (remainder normalizzato)")
        @CsvSource({
            "10, 3, 1",    // 10 = 3*3 + 1
            "10, -3, 1",   // 10 = (-3)*(-3) + 1. (10 % -3 = 1 in Java)
            "-10, 3, 2",   // -10 = (-4)*3 + 2. (Garantisce resto >= 0)
            "-10, -3, 2"   // -10 = 4*(-3) + 2. (Garantisce resto >= 0)
        })
        void remainderNormalised(long dividend, long divisor, long expectedRemainder) {
            assertEquals(si(expectedRemainder), si(dividend).remainder(si(divisor)));
            assertTrue(si(expectedRemainder).getValue() >= 0, "Il resto deve essere >= 0");
            
            // Verifica la proprietà fondamentale: a = q*b + r
            SignedInt q = si(dividend).quotient(si(divisor));
            SignedInt r = si(dividend).remainder(si(divisor));
            SignedInt check = q.multiply(si(divisor)).add(r);
            assertEquals(si(dividend), check, "La verifica a = q*b + r deve essere soddisfatta.");
        }
        
        @Test
        void remainderDivisionByZero() {
            assertThrows(ArithmeticException.class, () -> 
                si(10).remainder(SignedInt.ZERO), 
                "La divisione per zero dovrebbe lanciare un'eccezione"
            );
        }
    }

    // ======================================================================================
    // CAPACITÀ: COMPARABLE ED EQUALITY
    // ======================================================================================

    @Test
    void compareToAndModulus() {
        SignedInt a = si(10);
        SignedInt b = si(20);
        SignedInt c = si(-15);

        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(si(10)));
        
        assertEquals(10.0, a.modulus());
        assertEquals(15.0, c.modulus());
    }

    // ======================================================================================
    // CAPACITÀ: CREATABLEFROMDOUBLE
    // ======================================================================================

    @Nested
    @DisplayName("Creazione da Double (valueOf)")
    class CreatableFromDoubleTests {
        
        @ParameterizedTest(name = "valueOf({0}) -> {1}")
        @CsvSource({"10.75, 10", "-3.14, -3", "0.999, 0"})
        void valueOfValid(double input, long expected) {
            SignedInt result = new SignedInt(0).valueOf(input); // Chiamata tramite istanza dummy
            assertEquals(si(expected), result);
            assertEquals(expected, result.getValue());
        }
        
        @Test
        void valueOfLongMinValue() {
            SignedInt result = new SignedInt(0).valueOf((double) Long.MIN_VALUE);
            assertEquals(si(Long.MIN_VALUE), result);
        }

        @Test
        void valueOfLongMaxValue() {
            SignedInt result = new SignedInt(0).valueOf((double) Long.MAX_VALUE);
            assertEquals(si(Long.MAX_VALUE), result);
        }

        @ParameterizedTest
        //@ValueSource(doubles = {9.223372036854776E18, -9.223372036854776E18}) // Valori appena fuori MAX/MIN
        @ValueSource(doubles = {
                9.223372036854776E18 + 500.0, // Valore leggermente superiore a MAX_VALUE (per forzare il confronto)
                -9.223372036854776E18 - 500.0, // Valore leggermente inferiore a MIN_VALUE
                Double.MAX_VALUE,
                Double.MIN_VALUE // Controlliamo anche questo caso
            })
        void valueOfOverflow(double input) {
            assertThrows(ArithmeticException.class, () -> 
                si(0).valueOf(input), 
                "valueOf dovrebbe lanciare ArithmeticException per overflow double"
            );
        }
    }
}