package net.gommagomma.smfn.math.algebra.numeric;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import net.gommagomma.smfn.math.utils.MathConstants;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Real: Test delle proprietà di Campo e Capacità Matematiche")
class RealTest {

    // TOLERANZA: Usata per assertEquals(double, double, delta)
    // Assumiamo che MathConstants.EPSILON sia ragionevole (es. 1e-15).
    private static final double EPSILON = MathConstants.EPSILON; 

    // Helper per una creazione rapida e confronto
    private Real r(double value) {
        return new Real(value);
    }

    // Helper per assertEquals con Real e TOLERANCE
    private void assertRealEquals(Real expected, Real actual) {
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   "Expected: " + expected.getValue() + " but was: " + actual.getValue());
    }

    // ======================================================================================
    // COSTRUTTORE E PROPRIETÀ BASE
    // ======================================================================================

    @Test
    @DisplayName("Costruttore, Getter, Costanti e Copia")
    void constructorAndBasicProps() {
        Real pi = r(Math.PI);
        assertEquals(Math.PI, pi.getValue(), EPSILON);
        assertEquals("3.141592653589793", pi.toString()); // Dipende dalla precisione Java

        assertEquals(0.0, RealFactory.getInstance().zero().getValue(), 0);
        assertEquals(1.0, RealFactory.getInstance().one().getValue(), 0);
        
        Real copy = pi.copy();
        assertRealEquals(pi, copy);
        assertNotSame(pi, copy);
    }

    @Test
    @DisplayName("isMathematicallyEqualTo (Tolleranza)")
    void mathematicalEquality() {
        Real a = r(10.0);
        Real b = r(10.0 + (EPSILON / 2.0)); // Entro la tolleranza
        Real c = r(10.0 + (EPSILON * 2.0)); // Fuori dalla tolleranza

        assertTrue(a.isMathematicallyEqualTo(a));
        assertTrue(a.isMathematicallyEqualTo(b));
        assertFalse(a.isMathematicallyEqualTo(c));
        assertFalse(a.isMathematicallyEqualTo(null));
    }

    @Test
    @DisplayName("equals/hashCode (Bit-level)")
    void equalsAndHashCode() {
        Real a = r(10.0);
        Real b = r(10.0);
        Real c = r(10.0000000000001);

        // Standard equals verifica i bit, non la tolleranza
        assertTrue(a.equals(b));
        assertEquals(a.hashCode(), b.hashCode());
        assertFalse(a.equals(c));
    }

    // ======================================================================================
    // OPERAZIONI DI CAMPO (FIELD OPERATIONS)
    // ======================================================================================

    @Nested
    @DisplayName("Operazioni Additive e Sottrazione")
    class AdditiveOperations {
        
        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({"5.5, 3.2, 8.7", "-5.0, 3.0, -2.0", "10.1, -10.1, 0.0"})
        void add(double a, double b, double expected) {
            assertRealEquals(r(expected), r(a).add(r(b)));
        }

        @Test
        void negateAndSubtract() {
            assertRealEquals(r(-15.7), r(15.7).negate());
            assertRealEquals(r(6.0), r(10.0).subtract(r(4.0)));
        }
        
        @Test
        void identityAndInverse() {
            assertRealEquals(r(5.0), r(5.0).add(RealFactory.getInstance().zero()));
            assertRealEquals(RealFactory.getInstance().zero(), r(5.0).add(r(5.0).negate()));
        }
    }

    @Nested
    @DisplayName("Operazioni Moltiplicative e Inverso")
    class MultiplicativeOperations {
        
        @ParameterizedTest(name = "{0} * {1} = {2}")
        @CsvSource({"5.0, 3.0, 15.0", "-5.0, 3.0, -15.0", "10.0, 0.0, 0.0", "-2.0, -2.0, 4.0"})
        void multiply(double a, double b, double expected) {
            assertRealEquals(r(expected), r(a).multiply(r(b)));
        }

        @Test
        void inverse() {
            assertRealEquals(r(0.25), r(4.0).inverse());
            assertRealEquals(r(-2.0), r(-0.5).inverse());
        }

        @Test
        void inverseOfZeroThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                RealFactory.getInstance().zero().inverse(), 
                "L'inverso di zero dovrebbe lanciare ArithmeticException"
            );
        }

        @Test
        void identity() {
            assertRealEquals(r(7.5), r(7.5).multiply(RealFactory.getInstance().one()));
            assertRealEquals(RealFactory.getInstance().one(), r(7.5).multiply(r(7.5).inverse()));
        }
    }

    @Nested
    @DisplayName("Potenza (ExponentiableElement)")
    class PowerOperations {
        
        @ParameterizedTest(name = "{0}^{1} = {2}")
        @CsvSource({"5.0, 3, 125.0", "4.0, 0, 1.0", "2.0, -1, 0.5", "10.0, 2, 100.0"})
        void power(double base, int exp, double expected) {
            assertRealEquals(r(expected), r(base).power(exp));
        }

        @Test
        void powerZeroNegativeExponentThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                RealFactory.getInstance().zero().power(-1), 
                "Zero elevato a potenza negativa dovrebbe lanciare ArithmeticException"
            );
        }
    }
    
    // ======================================================================================
    // CAPACITÀ AGGIUNTIVE
    // ======================================================================================

    @Nested
    @DisplayName("Radice Quadrata (SqrtableElement)")
    class SqrtOperations {
        
        @Test
        void sqrtPositive() {
            assertRealEquals(r(5.0), r(25.0).sqrt());
            assertRealEquals(r(Math.sqrt(2.0)), r(2.0).sqrt());
            assertRealEquals(r(0.0), r(0.0).sqrt());
        }

        @Test
        void sqrtNegativeThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                r(-4.0).sqrt(), 
                "La radice quadrata di un numero negativo dovrebbe lanciare ArithmeticException"
            );
        }

        @Test
        void sqrtVerification() {
            Real input = r(2.0);
            Real result = input.sqrt();
            
            // Verifica che (sqrt(x))^2 sia matematicamente uguale a x
            Real squaredResult = result.multiply(result);
            
            // La verifica è sempre assertRealEquals(input, squaredResult);
            assertRealEquals(r(25.0), r(25.0).sqrt().multiply(r(25.0).sqrt()));
            
            // Verifica su 2.0 (il test problematico)
            assertRealEquals(input, squaredResult); // assertRealEquals(r(2.0), r(2.0).sqrt().multiply(r(2.0).sqrt()))
        }
    }

    @Nested
    @DisplayName("Norma e Modulo (NormableElement & abs)")
    class NormOperations {
        
        @Test
        void normAndModulus() {
            Real positive = r(10.5);
            Real negative = r(-10.5);
            
            assertRealEquals(positive, positive.norm());
            assertRealEquals(positive, negative.norm());
            
            assertEquals(10.5, positive.modulus(), 0);
            assertEquals(10.5, negative.modulus(), 0);
        }
    }
    
    @Nested
    @DisplayName("Comparazione (ComparableElement)")
    class ComparisonTests {
        
        @Test
        void compareTo() {
            Real a = r(10.0);
            Real b = r(20.0);
            
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertEquals(0, a.compareTo(r(10.0)));
            
            // Verifica la tolleranza: compareTo non usa la tolleranza
            assertTrue(r(10.0).compareTo(r(10.0 + MathConstants.EPSILON)) < 0);

            // Verifica che una differenza DUE VOLTE maggiore di EPSILON dia FALSE
            final double deltaBeyondEpsilon = MathConstants.EPSILON * 2.0; 
            assertFalse(r(10.0).isMathematicallyEqualTo(r(10.0 + deltaBeyondEpsilon)), "La differenza > EPSILON dovrebbe restituire false.");
            
            // Verifica che una differenza MINORE di EPSILON dia TRUE
            final double deltaWithinEpsilon = MathConstants.EPSILON / 2.0;
            assertTrue(r(10.0).isMathematicallyEqualTo(r(10.0 + deltaWithinEpsilon)), "La differenza < EPSILON dovrebbe restituire true.");
        }
    }
    
    @Nested
    @DisplayName("Creazione da Double (CreatableFromDouble)")
    class CreatableFromDoubleTests {
        
        @ParameterizedTest
        @ValueSource(doubles = {123.45, -99.99, 0.0, Math.PI})
        void valueOfValid(double input) {
            Real result = RealFactory.getInstance().of(input); // Chiamata tramite istanza
            assertEquals(input, result.getValue(), 0);
        }
        
        @Test
        void valueOfSpecialValues() {
            assertRealEquals(r(Double.MAX_VALUE), RealFactory.getInstance().of(Double.MAX_VALUE));
            assertRealEquals(r(Double.MIN_VALUE), RealFactory.getInstance().of(Double.MIN_VALUE));
            assertRealEquals(r(Double.NaN), RealFactory.getInstance().of(Double.NaN));
            assertRealEquals(r(Double.POSITIVE_INFINITY), RealFactory.getInstance().of(Double.POSITIVE_INFINITY));
        }
    }
}