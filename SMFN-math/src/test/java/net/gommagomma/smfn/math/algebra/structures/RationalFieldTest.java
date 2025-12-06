package net.gommagomma.smfn.math.algebra.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.RationalFactory;

@DisplayName("RationalField: Test della Struttura del Campo (Q)")
class RationalFieldTest {

    private final RationalField RATIONAL_FIELD = RationalField.getInstance();
    
    // Helper per creare un Rational
    private Rational r(long num, long den) {
        return new Rational(num, den);
    }
    
    private Rational r(long value) {
        return new Rational(value);
    }
    
    // Helper per verificare l'uguaglianza (Rational usa equals/isMathematicallyEqualTo che confrontano le forme semplici)
    private void assertRationalEquals(Rational expected, Rational actual, String message) {
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   message + String.format(" | Atteso: %s, Ottenuto: %s", 
                                          expected.toString(), actual.toString()));
    }

    // ======================================================================================
    // 1. PROPRIETÀ BASE DELLA STRUTTURA
    // ======================================================================================

    @Test
    @DisplayName("Proprietà Statiche e Identità")
    void basicProperties() {
        assertNotNull(RATIONAL_FIELD, "L'istanza non deve essere null.");
        assertEquals("Rational Field (Q)", RATIONAL_FIELD.getName(), "Il nome del campo non è corretto.");
        assertSame(RationalField.INSTANCE, RATIONAL_FIELD, "Deve essere singleton.");
    }

    @Test
    @DisplayName("contains: Inclusione degli elementi")
    void containsElements() {
        // Un RationalField contiene ogni oggetto Rational non nullo.
        assertTrue(RATIONAL_FIELD.contains(r(1, 2)), "Contiene 1/2");
        assertTrue(RATIONAL_FIELD.contains(r(-5)), "Contiene -5");
        assertTrue(RATIONAL_FIELD.contains(RationalFactory.getInstance().zero()), "Contiene ZERO");
        
        // Non deve contenere null
        assertFalse(RATIONAL_FIELD.contains(null), "Non contiene null");
    }

    @Test
    @DisplayName("Identità Additiva e Moltiplicativa")
    void identities() {
        assertRationalEquals(RationalFactory.getInstance().zero(), RATIONAL_FIELD.additiveIdentity(), "Identità Additiva (Zero)");
        assertRationalEquals(RationalFactory.getInstance().one(), RATIONAL_FIELD.multiplicativeIdentity(), "Identità Moltiplicativa (One)");
        assertSame(RationalFactory.getInstance().zero(), RATIONAL_FIELD.additiveIdentity());
        assertSame(RationalFactory.getInstance().one(), RATIONAL_FIELD.multiplicativeIdentity());
    }

    // ======================================================================================
    // 2. VERIFICA DEGLI ASSIOMI DI CAMPO (PROPRIETÀ FUNZIONALI)
    // ======================================================================================
    
    // Usiamo tre valori razionali generici
    private final Rational a = r(1, 2); // 1/2
    private final Rational b = r(-3, 4); // -3/4
    private final Rational c = r(5, 6); // 5/6

    private final Rational ZERO = RATIONAL_FIELD.additiveIdentity();
    private final Rational ONE = RATIONAL_FIELD.multiplicativeIdentity();

    // --- GRUPPO ABELIANO ADDITIVO ---
    
    @Nested
    @DisplayName("Assiomi Additivi")
    class AdditiveAxioms {
        
        @Test
        @DisplayName("Chiusura (Closure)")
        void closure() {
            // (1/2 + 5/6) = 8/6 = 4/3
            Rational result = a.add(c); 
            assertTrue(RATIONAL_FIELD.contains(result), "(a + c) è chiuso");
            assertRationalEquals(r(4, 3), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("Associatività")
        void associativity() {
            // (a + b) + c == a + (b + c)
            Rational left = a.add(b).add(c);
            Rational right = a.add(b.add(c));
            assertRationalEquals(left, right, "L'addizione deve essere associativa.");
        }
        
        @Test
        @DisplayName("Identità Additiva (Zero)")
        void identity() {
            // a + 0 == a
            assertRationalEquals(a, a.add(ZERO), "a + 0 == a");
            // 0 + a == a
            assertRationalEquals(a, ZERO.add(a), "0 + a == a");
        }

        @Test
        @DisplayName("Inverso Additivo (Negazione)")
        void inverse() {
            // a + (-a) == 0
            Rational negated = a.negate();
            assertRationalEquals(ZERO, a.add(negated), "a + (-a) == 0");
            
            // (-3/4) + (3/4) = 0
            assertRationalEquals(ZERO, b.add(b.negate()), "b + (-b) == 0");
        }
        
        @Test
        @DisplayName("Commutatività")
        void commutativity() {
            // a + b == b + a
            assertRationalEquals(a.add(b), b.add(a), "L'addizione deve essere commutativa.");
        }
    }

    // --- GRUPPO ABELIANO MOLTIPLICATIVO (per Q - {0}) ---

    @Nested
    @DisplayName("Assiomi Moltiplicativi")
    class MultiplicativeAxioms {
        
        @Test
        @DisplayName("Chiusura")
        void closure() {
            // (1/2 * 5/6) = 5/12
            Rational result = a.multiply(c);
            assertTrue(RATIONAL_FIELD.contains(result), "(a * c) è chiuso");
            assertRationalEquals(r(5, 12), result, "Verifica calcolo di chiusura");
        }

        @Test
        @DisplayName("Associatività")
        void associativity() {
            // (a * b) * c == a * (b * c)
            Rational left = a.multiply(b).multiply(c);
            Rational right = a.multiply(b.multiply(c));
            assertRationalEquals(left, right, "La moltiplicazione deve essere associativa.");
        }
        
        @Test
        @DisplayName("Identità Moltiplicativa (Uno)")
        void identity() {
            // a * 1 == a
            assertRationalEquals(a, a.multiply(ONE), "a * 1 == a");
            // 1 * a == a
            assertRationalEquals(a, ONE.multiply(a), "1 * a == a");
        }

        @Test
        @DisplayName("Inverso Moltiplicativo (Reciproco)")
        void inverse() {
            // a * a⁻¹ == 1
            Rational invA = a.inverse(); // 2/1
            assertRationalEquals(ONE, a.multiply(invA), "a * a⁻¹ == 1 (per a = 1/2)");
            
            // b * b⁻¹ == 1
            Rational invB = b.inverse(); // -4/3
            assertRationalEquals(ONE, b.multiply(invB), "b * b⁻¹ == 1 (per b = -3/4)");
        }
        
        @Test
        @DisplayName("Inverso di Zero (Eccezione)")
        void inverseOfZeroThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                ZERO.inverse(), 
                "L'inverso di zero deve lanciare ArithmeticException in un Campo."
            );
        }
        
        @Test
        @DisplayName("Commutatività")
        void commutativity() {
            // a * b == b * a
            assertRationalEquals(a.multiply(b), b.multiply(a), "La moltiplicazione deve essere commutativa.");
        }
    }

    // --- ASSIOMA DI DISTRIBUTIVITÀ ---

    @Nested
    @DisplayName("Assioma Distributivo")
    class DistributiveAxiom {
        
        @Test
        @DisplayName("Distributività della moltiplicazione sull'addizione")
        void distributivity() {
            // a * (b + c) == (a * b) + (a * c)
            Rational left = a.multiply(b.add(c));
            Rational right = a.multiply(b).add(a.multiply(c));
            
            assertRationalEquals(left, right, "La moltiplicazione deve essere distributiva.");
        }
    }
}