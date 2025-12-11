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

import net.gommagomma.smfn.math.algebra.numeric.Real;


@DisplayName("RealField: Test della Struttura del Campo (Field Axioms)")
class RealFieldTest {

    private final RealField REAL_FIELD = RealField.getInstance();
    
    // Helper per creare un Real
    private Real r(double value) {
        return new Real(value);
    }
    
    // Helper per verificare l'uguaglianza con tolleranza (matematica)
    private void assertRealEquals(Real expected, Real actual, String message) {
        assertTrue(expected.isMathematicallyEqualTo(actual), 
                   message + String.format(" | Atteso: %.12f, Ottenuto: %.12f", 
                                          expected.getValue(), actual.getValue()));
    }

    // ======================================================================================
    // 1. PROPRIETÃ€ BASE DELLA STRUTTURA
    // ======================================================================================

    @Test
    @DisplayName("ProprietÃ  Statiche e Naming")
    void basicProperties() {
        assertNotNull(REAL_FIELD, "L'istanza non deve essere null.");
        assertEquals("Real Field (R)", REAL_FIELD.getName(), "Il nome del campo non Ã¨ corretto.");
        assertEquals(REAL_FIELD, RealField.getInstance(), "Deve essere singleton.");
    }

    @Test
    @DisplayName("contains: Inclusione degli elementi")
    void containsElements() {
        // Numeri Reali Finiti
        assertTrue(REAL_FIELD.contains(r(5.0)), "Contiene 5.0");
        assertTrue(REAL_FIELD.contains(r(-12.34)), "Contiene -12.34");
        assertTrue(REAL_FIELD.contains(RealField.getInstance().zero()), "Contiene 0");
        
        // Elementi Non Finiti (Non dovrebbero essere inclusi in un Campo standard)
        assertFalse(REAL_FIELD.contains(r(Double.NaN)), "Non contiene NaN");
        assertFalse(REAL_FIELD.contains(r(Double.POSITIVE_INFINITY)), "Non contiene Infinity");
        assertFalse(REAL_FIELD.contains(r(Double.NEGATIVE_INFINITY)), "Non contiene -Infinity");
        
        // Null check
        assertFalse(REAL_FIELD.contains(null), "Non contiene null");
    }

    @Test
    @DisplayName("IdentitÃ  Additiva e Moltiplicativa")
    void identities() {
        assertRealEquals(r(0.0), REAL_FIELD.additiveIdentity(), "IdentitÃ  Additiva (Zero)");
        assertRealEquals(r(1.0), REAL_FIELD.multiplicativeIdentity(), "IdentitÃ  Moltiplicativa (One)");
        assertSame(RealField.getInstance().zero(), REAL_FIELD.additiveIdentity());
        assertSame(RealField.getInstance().one(), REAL_FIELD.multiplicativeIdentity());
    }

    // ======================================================================================
    // 2. VERIFICA DEGLI ASSIOMI DI CAMPO (PROPRIETÃ€ FUNZIONALI)
    // ======================================================================================
    
    // Usiamo tre valori generici
    private final Real a = r(5.0);
    private final Real b = r(-3.5);
    private final Real c = r(0.7);

    private final Real ZERO = REAL_FIELD.additiveIdentity();
    private final Real ONE = REAL_FIELD.multiplicativeIdentity();

    // --- GRUPPO ABELIANO ADDITIVO (Assiomi G1-G4) ---
    
    @Nested
    @DisplayName("Assiomi Additivi (Campo come Gruppo Abeliano Additivo)")
    class AdditiveAxioms {
        
        @Test
        @DisplayName("G1: Chiusura (Closure)")
        void closure() {
            // (a + b) Ã¨ un Real finito
            Real result = a.add(b);
            assertTrue(REAL_FIELD.contains(result), "(a + b) Ã¨ chiuso");
        }

        @Test
        @DisplayName("G2: AssociativitÃ ")
        void associativity() {
            // (a + b) + c == a + (b + c)
            Real left = a.add(b).add(c);
            Real right = a.add(b.add(c));
            assertRealEquals(left, right, "L'addizione deve essere associativa.");
        }
        
        @Test
        @DisplayName("G3: IdentitÃ  Additiva (Zero)")
        void identity() {
            // a + 0 == a
            assertRealEquals(a, a.add(ZERO), "a + 0 == a");
            // 0 + a == a
            assertRealEquals(a, ZERO.add(a), "0 + a == a");
        }

        @Test
        @DisplayName("G4: Inverso Additivo (Negazione)")
        void inverse() {
            // a + (-a) == 0
            Real negated = a.negate();
            assertRealEquals(ZERO, a.add(negated), "a + (-a) == 0");
        }
        
        @Test
        @DisplayName("G5: CommutativitÃ ")
        void commutativity() {
            // a + b == b + a
            assertRealEquals(a.add(b), b.add(a), "L'addizione deve essere commutativa.");
        }
    }

    // --- GRUPPO ABELIANO MOLTIPLICATIVO (per gli elementi non zero) (Assiomi M1-M5) ---

    @Nested
    @DisplayName("Assiomi Moltiplicativi (Campo come Gruppo Moltiplicativo)")
    class MultiplicativeAxioms {
        
        @Test
        @DisplayName("M1: Chiusura")
        void closure() {
            // (a * b) Ã¨ un Real finito
            Real result = a.multiply(b);
            assertTrue(REAL_FIELD.contains(result), "(a * b) Ã¨ chiuso");
        }

        @Test
        @DisplayName("M2: AssociativitÃ ")
        void associativity() {
            // (a * b) * c == a * (b * c)
            Real left = a.multiply(b).multiply(c);
            Real right = a.multiply(b.multiply(c));
            assertRealEquals(left, right, "La moltiplicazione deve essere associativa.");
        }
        
        @Test
        @DisplayName("M3: IdentitÃ  Moltiplicativa (Uno)")
        void identity() {
            // a * 1 == a
            assertRealEquals(a, a.multiply(ONE), "a * 1 == a");
            // 1 * a == a
            assertRealEquals(a, ONE.multiply(a), "1 * a == a");
        }

        @Test
        @DisplayName("M4: Inverso Moltiplicativo (Reciproco)")
        void inverse() {
            // Per tutti gli a != 0, a * aâ�»Â¹ == 1
            Real invA = a.inverse();
            assertRealEquals(ONE, a.multiply(invA), "a * aâ�»Â¹ == 1 (per a = 5.0)");
            
            // Test di un altro valore (b != 0)
            Real invB = b.inverse();
            assertRealEquals(ONE, b.multiply(invB), "b * bâ�»Â¹ == 1 (per b = -3.5)");
        }
        
        @Test
        @DisplayName("M4: Inverso di Zero (Eccezione)")
        void inverseOfZeroThrowsException() {
            assertThrows(ArithmeticException.class, () -> 
                ZERO.inverse(), 
                "L'inverso di zero deve lanciare ArithmeticException in un Campo."
            );
        }
        
        @Test
        @DisplayName("M5: CommutativitÃ ")
        void commutativity() {
            // a * b == b * a
            assertRealEquals(a.multiply(b), b.multiply(a), "La moltiplicazione deve essere commutativa.");
        }
    }

    // --- ASSIOMA DI DISTRIBUTIVITÃ€ (Assioma D) ---

    @Nested
    @DisplayName("Assioma Distributivo")
    class DistributiveAxiom {
        
        @Test
        @DisplayName("D: DistributivitÃ  della moltiplicazione sull'addizione")
        void distributivity() {
            // a * (b + c) == (a * b) + (a * c)
            Real left = a.multiply(b.add(c));
            Real right = a.multiply(b).add(a.multiply(c));
            
            assertRealEquals(left, right, "La moltiplicazione deve essere distributiva.");
        }
    }
}